package com.github.ryand6.sudokuweb.services.user;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtpEmail(String emailAddress, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailAddress);
        message.setSubject("Your account recovery code");
        message.setText("Your one-time recovery code is: " + otp + "\n\nThis code expires in 10 minutes.\n\nIf you did not request this, please ignore this email.");
        javaMailSender.send(message);
    }

}
