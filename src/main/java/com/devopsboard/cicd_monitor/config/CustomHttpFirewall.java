package com.devopsboard.cicd_monitor.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedException;


public class CustomHttpFirewall implements HttpFirewall {
    private static final Logger logger = LoggerFactory.getLogger(CustomHttpFirewall.class);

    @Override
    public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {
        String requestUri = request.getRequestURI();
        if (requestUri.contains("//")) {
            logger.warn("Allowing request with double slashes: {}", requestUri);
            // Allow URLs with double slashes
        }
        // Add other validations as needed
        return new FirewalledRequest(request) {
            @Override
            public void reset() {
                // No-op
            }
        };
    }

    @Override
    public HttpServletResponse getFirewalledResponse(HttpServletResponse response) {
        return response;
    }
}