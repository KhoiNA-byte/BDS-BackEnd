package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.BloodUnitDto;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.mapper.BloodUnitMapper;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.service.BloodUnitService;
import com.blooddonation.blood_donation_support_system.validator.MedicalFacilityStockValidator;
import com.blooddonation.blood_donation_support_system.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BloodUnitServiceImpl implements BloodUnitService {
    @Autowired
    private MedicalFacilityStockValidator validator;
    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    @Override
    public BloodUnitDto getBloodUnitById(Long id) {
        BloodUnit bloodUnit = validator.getBloodUnitOrThrow(id);
        return BloodUnitMapper.toDto(bloodUnit);
    }

    @Override
    public Page<BloodUnitDto> getAllBloodUnits(int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return  bloodUnitRepository.findAll(pageable).map(BloodUnitMapper::toDto);
    }
}
