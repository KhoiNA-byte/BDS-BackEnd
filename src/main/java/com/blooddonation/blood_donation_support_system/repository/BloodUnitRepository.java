package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodUnitRepository extends JpaRepository<BloodUnit, Long> {
    List<BloodUnit> findByEventId(Long eventId);
    Page<BloodUnit> findAll(Pageable pageable);
    BloodUnit findByDonorIdAndEvent_Id(Long donorId, Long eventId);
    BloodUnit findByProfileIdAndEvent_Id(Profile profileId, Long eventId);
}
