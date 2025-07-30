package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import com.blooddonation.blood_donation_support_system.dto.BlogRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface BlogRequestService {
    String createBlogRequest(BlogDto blogDto, MultipartFile thumbnail, String staffEmail);
    String updateBlogRequest(Long accountId, Long blogId, BlogDto blogDto, MultipartFile thumbnail);
    String deleteBlogRequest(Long accountId, Long blogId);
    String verifyBlogRequest(Long requestId, String action);
    Page<BlogRequestDto> getSortedPaginatedBlogRequests(int pageNumber, int pageSize, String sortBy, boolean ascending);
    BlogRequestDto getBlogRequestById(Long requestId);
    Page<BlogRequestDto> getMyBlogRequests(String email, int pageNumber, int pageSize, String sortBy, boolean ascending);
    BlogRequestDto getMyBlogRequestDetails(Long requestId, Long accountId);
}
