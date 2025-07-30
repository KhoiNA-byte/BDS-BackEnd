package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.service.AccountService;
import com.blooddonation.blood_donation_support_system.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserValidator validator;
    @Autowired
    private AccountMapper accountMapper;

    public AccountDto updateUserPassword(AccountDto accountDto, String oldPassword, String newPassword) {
        Account account = validator.getUserOrThrow(accountDto.getId());
        validator.validateUpdatePassword(oldPassword, account.getPassword(), newPassword);
        account.setPassword(passwordEncoder.encode(newPassword));
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    public AccountDto updateUserRole(Long accountId, String newRole) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setRole(Role.valueOf(newRole));
        Account updatedAccount = accountRepository.save(account);
        return AccountMapper.toDto(updatedAccount);
    }


    public AccountDto updateUserStatus(Long accountId, String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (status.equals("DISABLE")) {
            account.setStatus(AccountStatus.DISABLE);
        } else if (status.equals("ENABLE")) {
            account.setStatus(AccountStatus.ENABLE);
        }
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    public String createAccount(AccountDto accountDto) {
        if (accountRepository.findByEmail(accountDto.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        accountDto.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        Account account = AccountMapper.toEntity(accountDto,accountDto.getRole());
        accountRepository.save(account);
        return "Account created successfully";
    }

    public AccountDto getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return AccountMapper.toDto(account);
    }

    public Page<AccountDto> getAllAccounts(int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return accountRepository.findAll(pageable).map(AccountMapper::toDto);
    }

    public AccountDto setAccountAvatar(Long accountId, String avatarUrl) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setAvatar(avatarUrl);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }
}

