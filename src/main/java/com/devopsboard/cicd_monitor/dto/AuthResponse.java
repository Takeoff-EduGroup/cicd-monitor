package com.devopsboard.cicd_monitor.dto;

import com.devopsboard.cicd_monitor.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse
{
    private String token;
    private User user;
}
