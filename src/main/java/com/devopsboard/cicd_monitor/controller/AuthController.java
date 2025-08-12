package com.devopsboard.cicd_monitor.controller;

import com.devopsboard.cicd_monitor.dto.AuthRequest;
import com.devopsboard.cicd_monitor.dto.AuthResponse;
import com.devopsboard.cicd_monitor.dto.RegisterRequest;
import com.devopsboard.cicd_monitor.entity.User;
import com.devopsboard.cicd_monitor.enumerated.Role;
import com.devopsboard.cicd_monitor.repository.IUserRepository;
import com.devopsboard.cicd_monitor.service.impl.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AuthController {

    private final AuthService authService;
    private final IUserRepository repository;

    public AuthController(AuthService authService, IUserRepository repository) {
        this.authService = authService;
        this.repository = repository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute RegisterRequest request) throws IOException {
        HashMap<String, Object> res = new HashMap<>();
        try {
            authService.register(request);
            res.put("success", true);
            res.put("msg", "User registration successful!");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("err", "User registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            AuthResponse authResponse = authService.login(request);
            response.put("success", true);
            response.put("token", authResponse.getToken());
            response.put("user", authResponse.getUser());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            response.put("success", false);
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/all/users")
    public ResponseEntity<?> getAllUsers() {
        HashMap<String, Object> res = new HashMap<>();
        try {
            List<User> roleUsers = repository.findByRole(Role.USER);
            res.put("success", true);
            res.put("users", roleUsers);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("error", "Failed to fetch users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            repository.deleteById(id);
            res.put("success", true);
            res.put("msg", "User deleted successfully");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("err", "User not found for ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
    }
}