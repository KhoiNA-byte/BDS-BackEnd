package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.dto.MedicalFacilityStockDto;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.service.MedicalFacilityStockService;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-facility-stock")
public class MedicalFacilityStockController {

    @Autowired
    private MedicalFacilityStockService medicalFacilityStockService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/add-from-event/{eventId}")
    public ResponseEntity<String> addBloodUnitsFromEvent(@PathVariable Long eventId,
                                                         @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);

            String result = medicalFacilityStockService.addBloodUnitsToStockByEventId(eventId, accountDto.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding blood units to stock from event: " + e.getMessage());
        }
    }

    @PostMapping("/withdrawn")
    public ResponseEntity<Object> withdrawBlood(@RequestBody BloodRequestDto bloodRequestDto) {
        try {
            return ResponseEntity.ok(medicalFacilityStockService.withdrawBloodFromStock(bloodRequestDto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while withdrawing blood from stock: " + e.getMessage());
        }
    }

    @GetMapping("/check-stock")
    public ResponseEntity<String> checkStock(@CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = medicalFacilityStockService.updateBeforeWithdraw(accountDto.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while checking stock: " + e.getMessage());
        }
    }

    @GetMapping("/get-stock")
    public ResponseEntity<List<MedicalFacilityStockDto>> getStock() {
        return new ResponseEntity<>(medicalFacilityStockService.getAllAvailableBlood(), HttpStatus.OK);
    }

    @PostMapping("/get-stock-by-type")
    public ResponseEntity<List<MedicalFacilityStockDto>> getStockByType(
            @RequestBody List<ComponentType> componentTypes,
            @RequestParam BloodType bloodType) {
        return new ResponseEntity<>(medicalFacilityStockService.getAvailableBloodByType(bloodType,componentTypes), HttpStatus.OK);
    }
    @PostMapping("/add-blood-into-stock")
    public ResponseEntity<?> addBloodIntoStock(@RequestBody MedicalFacilityStockDto stockDto) {
        try {
            MedicalFacilityStockDto result = medicalFacilityStockService.addBloodIntoStock(stockDto);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding blood into stock: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStock(@PathVariable Long id) {
        try {
            medicalFacilityStockService.deleteStockById(id);
            return ResponseEntity.ok("Stock deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete stock: " + e.getMessage());
        }
    }
}