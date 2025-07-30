package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.DonationEventRequest;
import com.blooddonation.blood_donation_support_system.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationEventRequestRepository extends JpaRepository<DonationEventRequest, Long> {
    Page<DonationEventRequest> findByAccount(Account account, Pageable pageable);
}
