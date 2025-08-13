package com.devopsboard.cicd_monitor.controller;

import com.devopsboard.cicd_monitor.dto.DeploymentEventDto;
import com.devopsboard.cicd_monitor.entity.DeploymentEvent;
import com.devopsboard.cicd_monitor.service.IDeploymentEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {
    private final IDeploymentEventService svc;

    public WebhookController(IDeploymentEventService svc) {
        this.svc = svc;
    }

    @Value("${webhook.secret:}")
    private String webhookSecret;

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/ci")
    public ResponseEntity<?> receiveCIEvent(
            @RequestHeader(value = "X-Webhook-Token", required = false) String token,
            @Valid @RequestBody DeploymentEventDto dto) {

        // Check if the webhook token is missing or invalid
        if (token == null || !token.equals(webhookSecret)) {
            // Log the failed attempt for debugging
            logger.error("Invalid or missing webhook token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token or missing token.");
        }

        // Optionally log the incoming payload for debugging (ensure you don't log sensitive data)
        logger.info("Received CI event: " + dto);

        // Ensure the payload is not null and is valid
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid payload: Deployment event data is missing.");
        }

        try {
            // Save the event
            DeploymentEvent saved = svc.saveFromDto(dto);

            // Broadcast saved event to WebSocket subscribers for real-time updates
            svc.broadcastEvent(saved);

            // Return success response with saved event data
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            // Log the exception details
            logger.error("Error processing CI event: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the CI event: " + e.getMessage());
        }
    }


    // Example: GET /api/webhook/events/recent?page=0&size=20
    @GetMapping("/events/recent")
    public ResponseEntity<Page<DeploymentEvent>> recent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<DeploymentEvent> events = svc.recent(PageRequest.of(page, size));
        return ResponseEntity.ok(events);
    }
}