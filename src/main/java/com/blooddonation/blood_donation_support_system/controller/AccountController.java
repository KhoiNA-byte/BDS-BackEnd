package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.AccountRoleUpdateDto;
import com.blooddonation.blood_donation_support_system.dto.AccountStatusUpdateDto;
import com.blooddonation.blood_donation_support_system.dto.UpdatePasswordDto;
import com.blooddonation.blood_donation_support_system.service.AccountService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/user/account")
public class AccountController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AccountService accountService;

    // Change User password
    @PutMapping("/update-password")
    public ResponseEntity<Object> updatePassword(@CookieValue("jwt-token") String jwtToken,
                                                 @Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(jwtToken);
            AccountDto updatedAccount = accountService.updateUserPassword(accountDto,
                    updatePasswordDto.getOldPassword(),
                    updatePasswordDto.getNewPassword());
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating password");
        }
    }

    // Get User Account Info
    @GetMapping()
    public ResponseEntity<Object> account(@CookieValue(value = "jwt-token") String jwtToken) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(jwtToken);
            return ResponseEntity.ok(accountDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information");
        }
    }

    // Show a list of all accounts
    @GetMapping("/list-account")
    public ResponseEntity<Page<AccountDto>> getAccountList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Page<AccountDto> accountDtoList = accountService.getAllAccounts(page, size, sortBy, ascending);
        return ResponseEntity.ok(accountDtoList);
    }

    // Update a user's role
    @PutMapping("/{accountId}/role")
    public ResponseEntity<Object> updateAccountRole(
            @PathVariable Long accountId,
            @RequestBody AccountRoleUpdateDto roleUpdate) {
        try {
            AccountDto updatedAccount = accountService.updateUserRole(accountId, roleUpdate.getRole().name());
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update a user's status(enable/disable)
    @PutMapping("/{accountId}/status")
    public ResponseEntity<Object> updateAccountStatus(@PathVariable Long accountId,
                                                      @Valid @RequestBody AccountStatusUpdateDto statusUpdate) {
        try {
            AccountDto updatedAccount = accountService.updateUserStatus(accountId, statusUpdate.getStatus().name());
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("list-account/create")
    public ResponseEntity<Object> createAccount(@Valid @RequestBody AccountDto accountDto) {
        try {
            String result = accountService.createAccount(accountDto);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Show specific account
    @GetMapping("list-account/{accountId}")
    public ResponseEntity<Object> getAccountById(@PathVariable Long accountId) {
        try {
            AccountDto accountDto = accountService.getAccountById(accountId);
            return ResponseEntity.ok(accountDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information by email");
        }
    }

    // Add account avatar
    @PostMapping("/{accountId}/avatar")
    public ResponseEntity<Object> addAccountAvatar(@PathVariable Long accountId, @RequestParam("avatar") MultipartFile avatarFile) {
        try {
            if (avatarFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Avatar file is empty");
            }
            String uploadDir = "uploads/avatars/";
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = accountId + "_" + avatarFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            avatarFile.transferTo(filePath);
            String avatarUrl = "/" + uploadDir + fileName;
            AccountDto updatedAccount = accountService.setAccountAvatar(accountId, avatarUrl);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload avatar: " + e.getMessage());
        }
    }

    // Update account avatar
    @PutMapping("/{accountId}/avatar")
    public ResponseEntity<Object> updateAccountAvatar(@PathVariable Long accountId, @RequestParam("avatar") MultipartFile avatarFile) {
        try {
            if (avatarFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Avatar file is empty");
            }
            String uploadDir = "uploads/avatars/";
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = accountId + "_" + avatarFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            avatarFile.transferTo(filePath);
            String avatarUrl = "/" + uploadDir + fileName;
            AccountDto updatedAccount = accountService.setAccountAvatar(accountId, avatarUrl);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update avatar: " + e.getMessage());
        }
    }
}





