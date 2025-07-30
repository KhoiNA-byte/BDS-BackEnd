package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;
import com.blooddonation.blood_donation_support_system.enums.Urgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    BloodRequest findById(int id);

    @Query("SELECT br FROM BloodRequest br LEFT JOIN FETCH br.componentRequests")
    List<BloodRequest> findAllWithComponents();

    List<BloodRequest> findAllByUrgency(Urgency urgency);
}
