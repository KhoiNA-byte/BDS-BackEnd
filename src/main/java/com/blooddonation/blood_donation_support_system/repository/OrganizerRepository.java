package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.Organizer;
import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    Optional<Organizer> findByEmail(String email);

    List<Organizer> findByStatus(AccountStatus status);

    Page<Organizer> findByStatus(AccountStatus status, Pageable pageable);

    @Query("SELECT o FROM Organizer o WHERE o.status = :status AND " +
           "(LOWER(o.organizationName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.contactPersonName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Organizer> findByStatusAndSearchTerm(@Param("status") AccountStatus status,
                                             @Param("searchTerm") String searchTerm,
                                             Pageable pageable);

    @Query("SELECT o FROM Organizer o WHERE " +
           "(LOWER(o.organizationName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.contactPersonName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Organizer> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    List<Organizer> findByCity(String city);

    List<Organizer> findByCityAndStatus(String city, AccountStatus status);

    @Query("SELECT o FROM Organizer o WHERE o.createdBy.id = :accountId")
    List<Organizer> findByCreatedByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT o FROM Organizer o WHERE o.createdBy.id = :accountId AND o.status = :status")
    List<Organizer> findByCreatedByAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") AccountStatus status);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT COUNT(o) FROM Organizer o WHERE o.status = :status")
    long countByStatus(@Param("status") AccountStatus status);

    @Query("SELECT COUNT(de) FROM DonationEvent de WHERE de.organizer.id = :organizerId")
    long countEventsByOrganizerId(@Param("organizerId") Long organizerId);

    @Query("SELECT COUNT(de) FROM DonationEvent de WHERE de.organizer.id = :organizerId AND de.status = :status")
    long countEventsByOrganizerIdAndStatus(@Param("organizerId") Long organizerId, @Param("status") AccountStatus status);
}
