package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import com.blooddonation.blood_donation_support_system.dto.BlogRequestDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Blog;
import com.blooddonation.blood_donation_support_system.entity.BlogRequest;
import com.blooddonation.blood_donation_support_system.enums.BlogRequestStatus;
import com.blooddonation.blood_donation_support_system.enums.BlogStatus;
import com.blooddonation.blood_donation_support_system.enums.CRUDType;
import com.blooddonation.blood_donation_support_system.mapper.BlogMapper;
import com.blooddonation.blood_donation_support_system.mapper.BlogRequestMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.BlogRepository;
import com.blooddonation.blood_donation_support_system.repository.BlogRequestRepository;
import com.blooddonation.blood_donation_support_system.service.BlogRequestService;
import com.blooddonation.blood_donation_support_system.validator.DonationEventValidator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BlogRequestServiceImpl implements BlogRequestService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BlogRequestRepository blogRequestRepository;
    @Autowired
    private DonationEventValidator validator;

    private String uploadDir = System.getenv("UPLOAD_DIR");

    @PostConstruct
    public void init() {
        // Use environment variable or default to app's working directory
        String envUploadDir = System.getenv("UPLOAD_DIR");
        if (envUploadDir != null && !envUploadDir.isEmpty()) {
            uploadDir = envUploadDir;
        } else {
            // Use relative path from application's working directory
            uploadDir = "./uploads";
        }

        log.info("Upload directory configured: {}", uploadDir);

        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            log.info("Upload directory ready: {}", uploadPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create upload directory: {}", uploadDir, e);
            throw new RuntimeException("Cannot create upload directory: " + uploadDir, e);
        }
    }

    @Autowired
    private BlogRepository blogRepository;

    public String createBlogRequest(BlogDto blogDto, MultipartFile thumbnail, String staffEmail) {
        Account staff = accountRepository.findByEmail(staffEmail);
        try {
            String thumbnailName = UUID.randomUUID() + "_thumbnail";
            Path thumbnailPath = Paths.get(uploadDir, thumbnailName);
            Files.createDirectories(thumbnailPath.getParent());

            Files.copy(thumbnail.getInputStream(), thumbnailPath, StandardCopyOption.REPLACE_EXISTING);

            blogDto.setThumbnail(thumbnailPath.toString());

            BlogRequest blogRequest = BlogRequestMapper.createBlog(blogDto, staff);
            blogRequestRepository.save(blogRequest);

            return "Blog Request created successfully";
        } catch (IOException e) {
            log.error("Error creating blog request: ", e);
            throw new RuntimeException("Failed to create blog request: " + e.getMessage());
        }
    }


    public String updateBlogRequest(Long accountId, Long blogId, BlogDto blogDto, MultipartFile thumbnail) {
        Account staff = validator.getDonorOrThrow(accountId);
        Blog blog = validator.getBlogOrThrow(blogId);
        validator.validateCorrectAuthor(staff, blog);

        try {

            // Handle thumbnail update
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbnailName = UUID.randomUUID() + "_" + thumbnail.getOriginalFilename();
                Path thumbnailPath = Paths.get(uploadDir, thumbnailName);
                Files.copy(thumbnail.getInputStream(), thumbnailPath, StandardCopyOption.REPLACE_EXISTING);
                blogDto.setThumbnail(uploadDir + thumbnailName);
            }

            BlogRequest blogRequest = BlogRequestMapper.updateBlog(blogDto, staff, blog);
            blogRequestRepository.save(blogRequest);

            return "Blog request created successfully";
        } catch (IOException e) {
            throw new RuntimeException("Failed to update blog: " + e.getMessage());
        }
    }

    public String deleteBlogRequest(Long accountId, Long blogId) {
        Account staff = validator.getDonorOrThrow(accountId);
        Blog blog = validator.getBlogOrThrow(blogId);
        validator.validateCorrectAuthor(staff, blog);

        BlogRequest blogRequest = BlogRequestMapper.deleteBlog(staff, blog);
        blogRequestRepository.save(blogRequest);
        return "Blog request created successfully";

    }

    public String verifyBlogRequest(Long requestId, String action) {
        BlogRequest blogRequest = validator.getBlogRequestOrThrow(requestId);
        Blog blog = blogRequest.getBlog();
        if (!blogRequest.getStatus().equals(BlogRequestStatus.PENDING)) {
            return "Blog request has already been verified";
        }
        validator.validateEventVerification(action);

        if (action.equalsIgnoreCase("reject")) {
            // Delete thumbnail if exists
            String thumbnail = blogRequest.getBlogDto().getThumbnail();
            if (thumbnail != null && !thumbnail.isEmpty()) {
                deleteFile(thumbnail);
            }

            // Delete content images if any
            Set<String> contentImages = extractImageUrls(blogRequest.getBlogDto().getContent());
            contentImages.forEach(this::deleteFile);

            blogRequest.setStatus(BlogRequestStatus.REJECTED);
            blogRequestRepository.save(blogRequest);
            return "Blog request rejected";
        }

        CRUDType type = blogRequest.getCrudType();
        switch (type) {
            case CREATE:
                createBlog(blogRequest.getBlogDto(), blogRequest.getAccount().getEmail());
                blogRequest.setStatus(BlogRequestStatus.APPROVED);
                blogRequestRepository.save(blogRequest);
                return "Blog request approved, Blog created successfully";
            case UPDATE:
                updateBlog(blogRequest.getBlog(), blogRequest.getBlogDto());
                blogRequest.setStatus(BlogRequestStatus.APPROVED);
                blogRequestRepository.save(blogRequest);
                return "Blog request approved, Blog updated successfully";
            case DELETE:
                deleteBlog(blogRequest.getBlog().getId());
                blogRequest.setStatus(BlogRequestStatus.APPROVED);
                blogRequestRepository.save(blogRequest);
                return "Blog request approved, Blog deleted successfully";
            default:
                return "Invalid request type";
        }
    }

    public void createBlog(BlogDto blogDto, String authorEmail) {
        Account author = accountRepository.findByEmail(authorEmail);
        Blog blog = BlogMapper.toEntity(blogDto, author);
        blogRepository.save(blog);
    }

    public void updateBlog(Blog blog, BlogDto blogDto) {
        try {
            // Handle content images cleanup
            String oldThumbnail = blog.getThumbnail();
            if (oldThumbnail != null) {
                Path oldThumbnailPath = Paths.get(uploadDir, oldThumbnail.replace(uploadDir, ""));
                Files.deleteIfExists(oldThumbnailPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update blog: " + e.getMessage());
        }

        Set<String> oldImages = extractImageUrls(blog.getContent());
        Set<String> newImages = extractImageUrls(blogDto.getContent());

        // Find and delete images that are no longer used
        oldImages.stream()
                .filter(img -> !newImages.contains(img))
                .forEach(img -> {
                    try {
                        Path imagePath = Paths.get(uploadDir, img.replace(uploadDir, ""));
                        Files.deleteIfExists(imagePath);
                    } catch (IOException e) {
                        log.warn("Failed to delete old image: " + img);
                    }
                });

        BlogMapper.update(blogDto, blog);
        blogRepository.save(blog);
    }

    public void deleteBlog(Long blogId) {
        Blog blog = validator.getBlogOrThrow(blogId);

        // Delete thumbnail if exists
        if (blog.getThumbnail() != null && !blog.getThumbnail().isEmpty()) {
            deleteFile(blog.getThumbnail());
        }

        // Delete content images if any
        Set<String> contentImages = extractImageUrls(blog.getContent());
        contentImages.forEach(this::deleteFile);

        // Set blog status to INACTIVE
        blog.setStatus(BlogStatus.INACTIVE);
        blogRepository.save(blog);
    }

    private Set<String> extractImageUrls(String content) {
        if (content == null) return new HashSet<>();

        Set<String> urls = new HashSet<>();
        Pattern pattern = Pattern.compile( uploadDir + "/[^\\s\"']+\\.(jpg|jpeg|png|gif)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }

    private void deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath.replace(uploadDir, ""));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete file: " + filePath, e);
        }
    }

    public Page<BlogRequestDto> getSortedPaginatedBlogRequests(int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        Page<BlogRequest> blogRequests = blogRequestRepository.findByStatus(BlogRequestStatus.PENDING, pageable);
        return blogRequests.map(BlogRequestMapper::toDto);
    }

    public BlogRequestDto getBlogRequestById(Long requestId) {
        BlogRequest request = blogRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Blog request not found"));
        return BlogRequestMapper.toDto(request);
    }

    public Page<BlogRequestDto> getMyBlogRequests(String email, int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Account account = accountRepository.findByEmail(email);

        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        Page<BlogRequest> requests = blogRequestRepository.findByAccount(account, pageable);
        return requests.map(BlogRequestMapper::toDto);
    }

    public BlogRequestDto getMyBlogRequestDetails(Long requestId, Long accountId) {
        Account account = validator.getDonorOrThrow(accountId);
        BlogRequest request = blogRequestRepository.findByAccountAndId(account, requestId);
        return BlogRequestMapper.toDto(request);
    }
}
