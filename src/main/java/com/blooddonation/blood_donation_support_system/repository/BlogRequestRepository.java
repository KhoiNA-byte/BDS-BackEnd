package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Blog;
import com.blooddonation.blood_donation_support_system.entity.BlogRequest;
import com.blooddonation.blood_donation_support_system.enums.BlogRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRequestRepository extends JpaRepository<BlogRequest, Long> {
    Page<BlogRequest> findByStatus(BlogRequestStatus status, Pageable pageable);
    Page<BlogRequest> findByAccount(Account account, Pageable pageable);
    BlogRequest findByAccountAndId(Account account, Long id);
}
