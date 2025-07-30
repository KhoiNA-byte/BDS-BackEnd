package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.BloodUnitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BloodUnitService {
    BloodUnitDto getBloodUnitById(Long id);
    Page<BloodUnitDto> getAllBloodUnits(int pageNumber, int pageSize, String sortBy, boolean ascending);
}
