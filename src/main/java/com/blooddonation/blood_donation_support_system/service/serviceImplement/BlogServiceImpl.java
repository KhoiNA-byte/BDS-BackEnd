package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Blog;
import com.blooddonation.blood_donation_support_system.enums.BlogStatus;
import com.blooddonation.blood_donation_support_system.mapper.BlogMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.BlogRepository;
import com.blooddonation.blood_donation_support_system.service.BlogService;
import com.blooddonation.blood_donation_support_system.validator.UserValidator;
//import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.io.File;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class BlogServiceImpl implements BlogService {

//    @Value("${upload.dir}")
//    private String uploadDir;
//
//    @PostConstruct
//    public void init() {
//        File dir = new File(uploadDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//    }

    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserValidator validator;

    public Page<BlogDto> getSortedPaginatedBlogs(int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return blogRepository.findAll(pageable).map(BlogMapper::toDto);
    }

    public BlogDto getBlogDetails(Long blogId) {
        Blog blog = validator.getBlogOrThrow(blogId);
        if (blog.getStatus() == BlogStatus.INACTIVE) {
            throw new RuntimeException("Blog is no longer available");
        }
        return BlogMapper.toDto(blog);
    }

    public Page<BlogDto> getMyBlogs(String email, int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Account staff = validator.getEmailOrThrow(email);
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Blog> blogs = blogRepository.findByAuthorAndStatus(staff, BlogStatus.ACTIVE, pageable);
        return blogs.map(BlogMapper::toDto);
    }

    public BlogDto getMyBlogDetails(Long blogId) {
        Blog blog = validator.getBlogOrThrow(blogId);
        if (blog.getStatus() == BlogStatus.INACTIVE) {
            throw new RuntimeException("Blog is no longer available");
        }
        return BlogMapper.toDto(blog);
    }



}

