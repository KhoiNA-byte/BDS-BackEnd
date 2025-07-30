package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final AccountRepository accountRepository;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, AccountRepository accountRepository) {
        this.jwtUtil = jwtUtil;
        this.accountRepository = accountRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws java.io.IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String token = jwtUtil.saveOAuth2User(oAuth2User);
        String email = oAuth2User.getAttribute("email");

        // Get account from repository to determine role
        Account account = accountRepository.findByEmail(email);

        // Set the JWT token in a cookie
        response.setHeader("Set-Cookie", "jwt-token=" + token + "; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=3600");

        // Redirect based on role
        String redirectUrl;
        if (account != null) {
            switch (account.getRole()) {
                case ADMIN:
                    redirectUrl = System.getenv("FRONTEND_URL").concat("/admins/dashboard");
                    break;
                case STAFF:
                    redirectUrl = System.getenv("FRONTEND_URL").concat("/staffs/dashboard");
                    break;
                default:
                    redirectUrl = System.getenv("FRONTEND_URL");
                    break;
            }
        } else {
            // Default to member page if account not found (though this shouldn't happen)
            redirectUrl = System.getenv("FRONTEND_URL");
        }

        response.sendRedirect(redirectUrl);
    }
}
