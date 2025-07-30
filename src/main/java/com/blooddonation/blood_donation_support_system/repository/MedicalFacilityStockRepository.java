package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.dto.ComponentRequestDto;
import com.blooddonation.blood_donation_support_system.entity.ComponentRequest;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface MedicalFacilityStockRepository extends JpaRepository<MedicalFacilityStock, Long> {
    Optional<MedicalFacilityStock> findByBloodTypeAndComponentType(BloodType bloodType, ComponentType componentType);
    @Query("SELECT m FROM MedicalFacilityStock m WHERE m.expiryDate >= CURRENT_DATE")
    List<MedicalFacilityStock> findAllAvailableBlood();
    @Query("SELECT m FROM MedicalFacilityStock m WHERE m.bloodType = :bloodType AND m.componentType IN :componentTypes AND m.expiryDate >= CURRENT_DATE")
    List<MedicalFacilityStock> findAvailableBloodByType(BloodType bloodType, List<ComponentType> componentTypes);
    @Query("SELECT m FROM MedicalFacilityStock m WHERE m.bloodType = :bloodType AND m.componentType = :componentType AND m.expiryDate >= CURRENT_DATE")
    List<MedicalFacilityStock> findAvailableBloodByType(BloodType bloodType, ComponentType componentType);
    @Modifying
    @Query("UPDATE MedicalFacilityStock s SET s.volume = s.volume - :volume WHERE s.bloodType = :bloodType AND s.componentType = :componentType AND s.volume >= :volume and s.expiryDate >= CURRENT_DATE")
    int withdrawBloodFromStock(BloodType bloodType, ComponentType componentType, Double volume);
    @Modifying
    @Query("UPDATE MedicalFacilityStock s SET s.volume = s.volume + :volume WHERE s.bloodType = :bloodType AND s.componentType = :componentType and s.expiryDate = :expiryDate")
    int addStock(BloodType bloodType, ComponentType componentType, Double volume, LocalDate expiryDate);
}
