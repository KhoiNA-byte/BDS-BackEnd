package com.blooddonation.blood_donation_support_system.util;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static com.blooddonation.blood_donation_support_system.enums.Role.MEMBER;

@Component
public class JwtUtil {

    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;
    private final SecretKey secretKey;
    private final long tokenAge = 24000 * 60 * 60;

    public JwtUtil(ProfileRepository profileRepository, AccountRepository accountRepository,
                   @Value("${jwt.secret}") String secret) {
        this.profileRepository = profileRepository;
        this.accountRepository = accountRepository;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenAge))
                .signWith(secretKey)
                .compact();
    }

    // This method is called when the user successfully logs in using OAuth2 and save the user
    public String saveOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            Account newAccount = new Account();
            newAccount.setPassword(UUID.randomUUID().toString());
            newAccount.setEmail(email);
            newAccount.setRole(MEMBER);
            newAccount.setStatus(AccountStatus.ENABLE);
            Profile profile = new Profile();
            profile.setName(name);
            newAccount.setProfile(profile);
            Account savedAccount = accountRepository.save(newAccount);
            Profile updateProfile = savedAccount.getProfile();
            updateProfile.setAccountId(savedAccount.getId());
            profileRepository.save(updateProfile);
        }
        return generateToken(email);
    }


    public boolean isTokenExpired(String token) {
        final Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiration.before(new Date(System.currentTimeMillis()));
    }

    public String extractBody(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public AccountDto extractUser(String token) {
        String email = extractBody(token);
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return AccountMapper.toDto(account);
    }


    public boolean validateToken(String token) {
        return (!isTokenExpired(token));
    }
}