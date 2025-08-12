package com.devopsboard.cicd_monitor.dto;

import com.devopsboard.cicd_monitor.enumerated.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest
{
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String mobileNumber;
    private String address;
    private MultipartFile profile;
}
