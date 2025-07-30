package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService {

    AccountDto updateUserPassword(AccountDto accountDto, String oldPassword, String newPassword);

//    List<AccountDto> getAllAccounts();

    Page<AccountDto> getAllAccounts(int pageNumber, int pageSize, String sortBy, boolean ascending);

    AccountDto updateUserRole(Long accountId, String newRole);

    AccountDto updateUserStatus(Long accountId, String status);

    String createAccount(AccountDto accountDto);

    AccountDto getAccountById(Long accountId);

    AccountDto setAccountAvatar(Long accountId, String avatarUrl);
}

