package com.betterhome.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendOtpRequest(
        @Email(message = "Enter a valid email address.")
        @NotBlank(message = "Email is required.")
        String email
) {
}
