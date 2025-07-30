package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.MedicalFacilityStockDto;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MedicalFacilityStockMapper {

    public static MedicalFacilityStockDto toDto(MedicalFacilityStock stock) {
        if (stock == null) return null;

        return MedicalFacilityStockDto.builder()
                .id(stock.getId())
                .volume(stock.getVolume())
                .bloodType(stock.getBloodType())
                .componentType(stock.getComponentType())
                .expiryDate(stock.getExpiryDate())
                .build();
    }

    public static MedicalFacilityStock toEntity(MedicalFacilityStockDto dto) {
        if (dto == null) return null;

        return MedicalFacilityStock.builder()
                .id(dto.getId())
                .volume(dto.getVolume())
                .bloodType(dto.getBloodType())
                .componentType(dto.getComponentType())
                .expiryDate(dto.getExpiryDate())
                .build();
    }

    public static MedicalFacilityStock fromBloodUnit(BloodUnit bloodUnit) {
        if (bloodUnit == null) return null;

        return MedicalFacilityStock.builder()
                .bloodType(bloodUnit.getBloodType())
                .volume(bloodUnit.getVolume())
                .componentType(bloodUnit.getComponentType())
                // TODO: Add expiry date calculation logic based on component type
                .build();
    }

    public static MedicalFacilityStock copyWithNewVolume(MedicalFacilityStock original,
                                                         double newVolume) {
        if (original == null) return null;

        return MedicalFacilityStock.builder()
                .id(original.getId()) // Keep same ID for updates
                .bloodType(original.getBloodType())
                .componentType(original.getComponentType())
                .volume(newVolume)
                .expiryDate(original.getExpiryDate())
                .build();
    }

    public static MedicalFacilityStock createWithdrawnStock(MedicalFacilityStock source,
                                                            double withdrawnVolume) {
        if (source == null) return null;

        return MedicalFacilityStock.builder()
                .bloodType(source.getBloodType())
                .componentType(source.getComponentType())
                .volume(withdrawnVolume)
                .expiryDate(source.getExpiryDate())
                .build();
    }

    public static MedicalFacilityStock createComponent(BloodType bloodType,
                                                            ComponentType componentType,
                                                            double volume,
                                                            LocalDate expiryDate) {
        return MedicalFacilityStock.builder()
                .bloodType(bloodType)
                .componentType(componentType)
                .volume(volume)
                .expiryDate(expiryDate)
                .build();
    }
}