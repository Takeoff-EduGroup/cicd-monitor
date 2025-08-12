package com.devopsboard.cicd_monitor.service;

public interface IEmailService {
    void sendOtp(String to, String otp);
}
