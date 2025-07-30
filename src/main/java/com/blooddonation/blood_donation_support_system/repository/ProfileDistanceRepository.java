package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.entity.ProfileDistance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileDistanceRepository extends JpaRepository<ProfileDistance, Long> {

    Optional<ProfileDistance> findByProfile(Profile profile);

    Optional<ProfileDistance> findByProfileId(Long profileId);

    @Query("SELECT pd FROM ProfileDistance pd WHERE pd.distanceInKilometers <= :maxDistance ORDER BY pd.distanceInKilometers ASC")
    List<ProfileDistance> findProfilesWithinDistance(@Param("maxDistance") Double maxDistance);

    @Query("SELECT pd FROM ProfileDistance pd WHERE pd.distanceInKilometers <= :maxDistance ORDER BY pd.distanceInKilometers ASC")
    Page<ProfileDistance> findProfilesWithinDistancePageable(@Param("maxDistance") Double maxDistance, Pageable pageable);

    @Query("SELECT pd FROM ProfileDistance pd ORDER BY pd.distanceInKilometers ASC")
    List<ProfileDistance> findAllOrderByDistanceAsc();

    @Query("SELECT pd FROM ProfileDistance pd ORDER BY pd.distanceInKilometers ASC")
    Page<ProfileDistance> findAllOrderByDistanceAscPageable(Pageable pageable);

    @Query("SELECT p FROM Profile p WHERE p.id NOT IN (SELECT pd.profile.id FROM ProfileDistance pd)")
    List<Profile> findProfilesWithoutDistance();

    boolean existsByProfile(Profile profile);

    boolean existsByProfileId(Long profileId);

    void deleteByProfile(Profile profile);

    void deleteByProfileId(Long profileId);
}