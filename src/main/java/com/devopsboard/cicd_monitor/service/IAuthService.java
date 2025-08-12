package com.devopsboard.cicd_monitor.service;

import com.devopsboard.cicd_monitor.dto.AuthRequest;
import com.devopsboard.cicd_monitor.dto.AuthResponse;
import com.devopsboard.cicd_monitor.dto.RegisterRequest;

import java.io.IOException;

public interface IAuthService {
    void register(RegisterRequest request) throws IOException;

    AuthResponse login(AuthRequest request);
}
