package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import com.blooddonation.blood_donation_support_system.dto.BlogRequestDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.service.BlogRequestService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/blog-request")
public class BlogRequestController {
    @Autowired
    private BlogRequestService blogRequestService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createBlog(
            @Valid @RequestPart("blog") BlogDto blogDto,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto account = jwtUtil.extractUser(token);
            String result = blogRequestService.createBlogRequest(blogDto, thumbnail, account.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create blog");
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<BlogRequestDto>> getPaginatedBlogRequests(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        try {
            Page<BlogRequestDto> requests = blogRequestService.getSortedPaginatedBlogRequests(
                    pageNumber, pageSize, sortBy, ascending);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/pending/{requestId}")
    public ResponseEntity<Object> getBlogRequestDetails(@PathVariable Long requestId) {
        try {
            BlogRequestDto requestDetails = blogRequestService.getBlogRequestById(requestId);
            return ResponseEntity.ok(requestDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/pending/{requestId}/verify")
    public ResponseEntity<String> verifyDonationRequest(
            @PathVariable Long requestId,
            @RequestParam String action) {
        try {
            String result = blogRequestService.verifyBlogRequest(requestId, action);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Page<BlogRequestDto>> getMyBlogRequests(
            @CookieValue("jwt-token") String token,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        try {
            AccountDto account = jwtUtil.extractUser(token);
            Page<BlogRequestDto> requests = blogRequestService.getMyBlogRequests(
                    account.getEmail(), pageNumber, pageSize, sortBy, ascending);
            return ResponseEntity.ok(requests);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/my-requests/{requestId}")
    public ResponseEntity<Object> getMyBlogRequestDetails(
            @CookieValue("jwt-token") String token,
            @PathVariable Long requestId) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            BlogRequestDto requestDetails = blogRequestService.getMyBlogRequestDetails(requestId, accountDto.getId());
            return ResponseEntity.ok(requestDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


}
