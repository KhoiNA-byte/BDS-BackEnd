package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.BloodUnitDto;
import com.blooddonation.blood_donation_support_system.service.BloodUnitService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blood-unit")
public class BloodUnitController {
    @Autowired
    private BloodUnitService bloodUnitService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list-unit")
    public ResponseEntity<Page<BloodUnitDto>> getAllBloodUnits(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
            Page<BloodUnitDto> bloodUnitDto = bloodUnitService.getAllBloodUnits(pageNumber, pageSize, sortBy, ascending);
            return ResponseEntity.ok(bloodUnitDto);
    }

    @GetMapping("/list-unit/{bloodUnitId}")
    public ResponseEntity<Object> getBloodUnitById(@PathVariable Long bloodUnitId) {
        try {
            BloodUnitDto bloodUnitDto = bloodUnitService.getBloodUnitById(bloodUnitId);
            return ResponseEntity.ok(bloodUnitDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
