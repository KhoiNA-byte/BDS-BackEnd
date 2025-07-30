package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import com.blooddonation.blood_donation_support_system.dto.BlogRequestDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Blog;
import com.blooddonation.blood_donation_support_system.entity.BlogRequest;
import com.blooddonation.blood_donation_support_system.enums.BlogRequestStatus;
import com.blooddonation.blood_donation_support_system.enums.CRUDType;
import org.springframework.stereotype.Component;

@Component
public class BlogRequestMapper {
    public static BlogRequestDto toDto(BlogRequest blogRequest) {
        if (blogRequest == null) {
            return null;
        }

        return BlogRequestDto.builder()
                .id(blogRequest.getId())
                .blogId(blogRequest.getId())
                .accountId(blogRequest.getId())
                .blog(blogRequest.getBlogDto())
                .status(blogRequest.getStatus())
                .crudType(blogRequest.getCrudType())
                .build();
    }

    public static BlogRequest toEntity(BlogRequestDto blogRequestDto, Account account, Blog blog) {
        if (blogRequestDto == null) {
            return null;
        }

        return BlogRequest.builder()
                .id(blogRequestDto.getId())
                .account(account)
                .blog(blog)
                .blogDto(blogRequestDto.getBlog())
                .status(blogRequestDto.getStatus())
                .crudType(blogRequestDto.getCrudType())
                .build();
    }

    public static BlogRequest createBlog(BlogDto blogDto, Account account) {
        if (blogDto == null) {
            return null;
        }
        blogDto.setAuthorName(account.getProfile().getName());
        return BlogRequest.builder()
                .id(blogDto.getId())
                .account(account)
                .blogDto(blogDto)
                .status(BlogRequestStatus.PENDING)
                .crudType(CRUDType.CREATE)
                .build();
    }

    public static BlogRequest updateBlog(BlogDto blogDto, Account account, Blog blog) {
        if (blogDto == null) {
            return null;
        }

        return BlogRequest.builder()
                .id(blogDto.getId())
                .account(account)
                .blog(blog)
                .blogDto(blogDto)
                .status(BlogRequestStatus.PENDING)
                .crudType(CRUDType.UPDATE)
                .build();
    }

    public static BlogRequest deleteBlog(Account account, Blog blog) {
        if (blog == null) {
            return null;
        }

        return BlogRequest.builder()
                .id(null)
                .account(account)
                .blog(blog)
                .blogDto(null)
                .status(BlogRequestStatus.PENDING)
                .crudType(CRUDType.DELETE)
                .build();
    }
}
