package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByPersonalId(String personalId);
    Optional<Profile> findFirstByPersonalId(String personalId);
    List<Profile> findByNextEligibleDonationDateLessThanEqual(LocalDate date);
    List<Profile> findAllByPersonalId(String personalId);
    
    // Comprehensive search method
    @Query("SELECT p FROM Profile p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.phone LIKE CONCAT('%', :query, '%') OR " +
           "LOWER(p.personalId) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Profile> searchByQuery(@Param("query") String query);
}
