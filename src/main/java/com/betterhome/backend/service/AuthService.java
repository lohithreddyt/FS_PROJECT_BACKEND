package com.betterhome.backend.service;

import com.betterhome.backend.dto.AuthRequest;
import com.betterhome.backend.dto.AuthResponse;
import com.betterhome.backend.dto.RegisterRequest;
import com.betterhome.backend.dto.SendOtpRequest;
import com.betterhome.backend.dto.VerifyOtpRequest;
import com.betterhome.backend.exception.AppException;
import com.betterhome.backend.model.AppUser;
import com.betterhome.backend.model.Role;
import com.betterhome.backend.repository.AppUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public static final String SESSION_OTP_EMAIL = "betterhome.otpEmail";
    public static final String SESSION_OTP_CODE = "betterhome.otpCode";
    public static final String SESSION_OTP_VERIFIED = "betterhome.otpVerified";
    public static final String SESSION_OTP_SENT_AT = "betterhome.otpSentAt";
    private static final long OTP_EXPIRATION_SECONDS = 300;

    private final AppUserRepository userRepository;
    private final SessionService sessionService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(AppUserRepository userRepository, SessionService sessionService, EmailService emailService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest request, HttpSession session) {
        String email = request.email().trim().toLowerCase();
        Role role = parseRole(request.role());

        if (role == Role.ADMIN && !email.endsWith("@betterhome.in")) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Admin accounts must use a @betterhome.in email address.");
        }
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new AppException(HttpStatus.CONFLICT, "Email is already registered.");
        }
        if (role == Role.USER) {
            if (!Boolean.TRUE.equals(session.getAttribute(SESSION_OTP_VERIFIED))) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Please verify OTP before registering.");
            }
            String otpEmail = (String) session.getAttribute(SESSION_OTP_EMAIL);
            if (otpEmail == null || !otpEmail.equals(email)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "OTP email does not match registration email.");
            }
            clearOtpSession(session);
        }

        AppUser user = userRepository.save(new AppUser(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password()),
                role
        ));
        sessionService.setSession(session, user);
        return toResponse(user);
    }

    public AuthResponse login(AuthRequest request, HttpSession session) {
        String email = request.email().trim().toLowerCase();
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Incorrect email or password."));

        if (request.password() == null || request.password().isBlank()) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Incorrect email or password.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Incorrect email or password.");
        }

        sessionService.setSession(session, user);
        clearOtpSession(session);
        return toResponse(user);
    }

    public void sendOtp(SendOtpRequest request, HttpSession session) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new AppException(HttpStatus.CONFLICT, "Email is already registered.");
        }

        String otpCode = String.format("%06d", (int) (Math.random() * 900000) + 100000);
        session.setAttribute(SESSION_OTP_EMAIL, email);
        session.setAttribute(SESSION_OTP_CODE, otpCode);
        session.setAttribute(SESSION_OTP_SENT_AT, System.currentTimeMillis());
        session.setAttribute(SESSION_OTP_VERIFIED, false);

        emailService.sendOtp(email, otpCode);
    }

    public void verifyOtp(VerifyOtpRequest request, HttpSession session) {
        String email = request.email().trim().toLowerCase();
        String code = request.code().trim();

        String sessionEmail = (String) session.getAttribute(SESSION_OTP_EMAIL);
        String sessionCode = (String) session.getAttribute(SESSION_OTP_CODE);
        Long sentAt = (Long) session.getAttribute(SESSION_OTP_SENT_AT);

        if (sessionEmail == null || sessionCode == null || sentAt == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "OTP not sent or session expired.");
        }
        if (!email.equals(sessionEmail)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "OTP email does not match.");
        }
        if (!sessionCode.equals(code)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid OTP code.");
        }
        if ((System.currentTimeMillis() - sentAt) / 1000 > OTP_EXPIRATION_SECONDS) {
            throw new AppException(HttpStatus.BAD_REQUEST, "OTP code has expired. Please request a new one.");
        }

        session.setAttribute(SESSION_OTP_VERIFIED, true);
    }

    private void clearOtpSession(HttpSession session) {
        session.removeAttribute(SESSION_OTP_CODE);
        session.removeAttribute(SESSION_OTP_SENT_AT);
        session.removeAttribute(SESSION_OTP_VERIFIED);
        session.removeAttribute(SESSION_OTP_EMAIL);
    }

    public AuthResponse getSessionUser(HttpSession session) {
        return toResponse(sessionService.requireUser(session));
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    private Role parseRole(String raw) {
        try {
            return Role.valueOf(raw.trim().toUpperCase());
        } catch (Exception ex) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role.");
        }
    }

    private AuthResponse toResponse(AppUser user) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name().toLowerCase());
    }
}
