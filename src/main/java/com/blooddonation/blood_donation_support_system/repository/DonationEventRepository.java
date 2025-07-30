package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import org.springframework.beans.PropertyValues;
import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonationEventRepository extends JpaRepository<DonationEvent, Long> {
    Page<DonationEvent> findByDonationDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<DonationEvent> findByAccountId(Long accountId, Pageable pageable);
    @Query("SELECT e FROM DonationEvent e LEFT JOIN FETCH e.registrations WHERE e.id = :id")
    DonationEvent findWithRegistrationsById(@Param("id") Long id);

    List<DonationEvent> findAllByDonationDate(LocalDate now);
}
