package com.betterhome.backend.service;

import com.betterhome.backend.dto.AuthRequest;
import com.betterhome.backend.dto.AuthResponse;
import com.betterhome.backend.dto.RegisterRequest;
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

    private final AppUserRepository userRepository;
    private final SessionService sessionService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(AppUserRepository userRepository, SessionService sessionService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
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
        AppUser user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Incorrect email or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Incorrect email or password.");
        }

        sessionService.setSession(session, user);
        return toResponse(user);
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
