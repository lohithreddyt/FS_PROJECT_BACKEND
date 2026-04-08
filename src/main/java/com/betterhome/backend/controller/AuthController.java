package com.betterhome.backend.controller;

import com.betterhome.backend.dto.AuthRequest;
import com.betterhome.backend.dto.AuthResponse;
import com.betterhome.backend.dto.RegisterRequest;
import com.betterhome.backend.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        return authService.register(request, session);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request, HttpSession session) {
        return authService.login(request, session);
    }

    @GetMapping("/session")
    public AuthResponse session(HttpSession session) {
        return authService.getSessionUser(session);
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        authService.logout(session);
    }
}
