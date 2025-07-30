package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.*;
import com.blooddonation.blood_donation_support_system.service.IBloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-request")
public class BloodRequestController {

    @Autowired
    private IBloodRequestService iBloodRequestService;

    @GetMapping("/request-list")
    public ResponseEntity<List<BloodRequestDto>> getAllRequest() {
        return new ResponseEntity<>(iBloodRequestService.findAllBloodRequest(), HttpStatus.OK);
    }

    @PostMapping("/create-request")
    public ResponseEntity<BloodRequestDto> createRequest(@RequestBody BloodRequestDto bloodRequestDto) {
        return new ResponseEntity<>(iBloodRequestService.createBloodRequest(bloodRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloodRequestDto> getRequestById(@PathVariable int id) {
        return new ResponseEntity<>(iBloodRequestService.findBloodRequestById(id), HttpStatus.OK);
    }

    @PostMapping("/add-donor")
    public ResponseEntity<?> addDonorToRequest(@RequestBody BloodRequestBatchDto bloodRequestBatchDto) {
        return new ResponseEntity<>(iBloodRequestService.addBloodRequestDonor(bloodRequestBatchDto.getBloodRequestDto(),
                bloodRequestBatchDto.getBloodUnitDto(), bloodRequestBatchDto.getProfileDto()), HttpStatus.OK);
    }

    @PostMapping("/fulfill-request")
    public ResponseEntity<?> fulfillRequest(@RequestBody BloodRequestDto bloodRequestDto) {
        return new ResponseEntity<>(iBloodRequestService.fulfillBloodRequest(bloodRequestDto), HttpStatus.OK);
    }

    @GetMapping("/emergency-request")
    public ResponseEntity<?>  getEmergencyBloodRequest() {
        return new ResponseEntity<>(iBloodRequestService.getEmergencyBloodRequest(), HttpStatus.OK);
    }
}


