package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface BlogService {
    Page<BlogDto> getSortedPaginatedBlogs(int pageNumber, int pageSize, String sortBy, boolean ascending);
    BlogDto getBlogDetails(Long blogId);
    BlogDto getMyBlogDetails(Long blogId);
    Page<BlogDto> getMyBlogs(String email, int pageNumber, int pageSize, String sortBy, boolean ascending);

}
