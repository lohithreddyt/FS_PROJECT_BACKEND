package com.betterhome.backend.service;

import com.betterhome.backend.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.from:no-reply@betterhome.in}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendOtp(String to, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("Your BetterHome OTP Code");
            message.setText("Your BetterHome OTP code is " + otpCode + ".\nIt expires in 5 minutes.");
            mailSender.send(message);
        } catch (MailException ex) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send OTP email. Check mail configuration.");
        }
    }
}
