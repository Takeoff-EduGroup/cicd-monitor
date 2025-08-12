package com.devopsboard.cicd_monitor.controller;

import com.devopsboard.cicd_monitor.service.impl.PipelineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Value("${github.webhook.secret}")
    private String webhookSecret;

    private final PipelineService pipelineService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    public WebhookController(PipelineService pipelineService, ObjectMapper objectMapper) {
        this.pipelineService = pipelineService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/github")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("X-Hub-Signature-256") String signatureHeader) {
        if (!isSignatureValid(payload, signatureHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        try {
            pipelineService.processWebhook(payload);
            return ResponseEntity.accepted().body("Webhook received and all will be processing");
        } catch (Exception e) {
            logger.error("Error processing GitHub webhook payload.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook processing failed: " + e.getMessage());
        }
    }

    private boolean isSignatureValid(String payload, String signatureHeader) {
        try {
            if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
                return false;
            }

            String signature = signatureHeader.substring(7);
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);

            byte[] calculatedHash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            // Use the standard HexFormat class for safer and more reliable conversion
            byte[] signatureBytes = HexFormat.of().parseHex(signature);

            // Use MessageDigest.isEqual for secure, constant-time byte array comparison
            return MessageDigest.isEqual(calculatedHash, signatureBytes);
        } catch (Exception e) {
            logger.error("Failed to validate webhook signature.", e);
            return false;
        }
    }
}