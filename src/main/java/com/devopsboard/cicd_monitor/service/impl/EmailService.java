package com.devopsboard.cicd_monitor.service.impl;

import com.devopsboard.cicd_monitor.service.IEmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your PrivLedger OTP Code");
        message.setText("Your OTP is: " + otp + "\nIt will expire in 5 minutes.");
        mailSender.send(message);
    }
}
