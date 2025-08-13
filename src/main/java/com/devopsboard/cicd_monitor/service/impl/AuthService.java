package com.devopsboard.cicd_monitor.service.impl;

import com.devopsboard.cicd_monitor.dto.AuthRequest;
import com.devopsboard.cicd_monitor.dto.AuthResponse;
import com.devopsboard.cicd_monitor.dto.RegisterRequest;
import com.devopsboard.cicd_monitor.entity.User;
import com.devopsboard.cicd_monitor.repository.IUserRepository;
import com.devopsboard.cicd_monitor.service.IAuthService;
import com.devopsboard.cicd_monitor.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AuthService implements IAuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final IUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    @Value("${upload.dir:src/main/resources/static/images}")
    private String uploadDir;

    public AuthService(IUserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    @Override
    public void register(RegisterRequest request) throws IOException {
        try {
            // Ensure upload directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = request.getProfile().getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            request.getProfile().transferTo(filePath);

            logger.info("File uploaded to: {}", filePath);

            // Proceed with user registration
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setMobileNumber(request.getMobileNumber());
            user.setAddress(request.getAddress());
            user.setProfile(filename);

            logger.info("Saving user: {}", user);
            userRepo.save(user);

        } catch (IOException | IllegalArgumentException e) {
            logger.error("Error during user registration: {}", e.getMessage(), e);
            throw new IOException("Error during user registration: " + e.getMessage(), e);
        }
    }


    @Override
    public AuthResponse login(AuthRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user);

        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);

        return response;
    }
}
