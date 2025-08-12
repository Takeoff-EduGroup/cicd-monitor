package com.devopsboard.cicd_monitor.service.impl;

import com.devopsboard.cicd_monitor.entity.Pipeline;
import com.devopsboard.cicd_monitor.repository.IPipelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PipelineService {

    private final IPipelineRepository pipelineRepository;
    private final JavaMailSender mailSender;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public PipelineService(
            IPipelineRepository pipelineRepository,
            JavaMailSender mailSender,
            SimpMessagingTemplate messagingTemplate,
            ObjectMapper objectMapper) {
        this.pipelineRepository = pipelineRepository;
        this.mailSender = mailSender;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Async // This annotation is crucial for asynchronous processing
    public void processWebhook(String payload) {
        try {
            Map<String, Object> payloadMap = objectMapper.readValue(payload, Map.class);
            Map<String, Object> workflowRun = (Map<String, Object>) payloadMap.get("workflow_run");

            if (workflowRun == null) {
                System.err.println("No workflow_run data in webhook payload");
                return;
            }
            String name = (String) workflowRun.get("name");
            String status = (String) workflowRun.get("status");
            String conclusion = (String) workflowRun.get("conclusion");
            Map<String, Object> headCommit = (Map<String, Object>) workflowRun.get("head_commit");
            String commit = headCommit != null ? (String) headCommit.get("id") : "unknown";
            String runTime = (String) workflowRun.get("created_at");

            if (name == null || status == null || runTime == null) {
                System.err.println("Missing required fields in workflow_run");
                return;
            }

            String pipelineStatus = "completed".equals(status) && "success".equals(conclusion) ? "SUCCESS" :
                    "completed".equals(status) && "failure".equals(conclusion) ? "FAILED" : "RUNNING";

            Pipeline pipeline = pipelineRepository.findByName(name)
                    .orElse(new Pipeline(null, name, pipelineStatus, LocalDateTime.parse(runTime), commit, 0.9));

            pipeline.setStatus(pipelineStatus);
            try {
                pipeline.setLastRunTime(LocalDateTime.parse(runTime));
            } catch (Exception e) {
                System.err.println("Error parsing runTime: " + runTime);
                pipeline.setLastRunTime(LocalDateTime.now());
            }
            pipeline.setLastCommit(commit);
            pipeline.setSuccessRate(calculateSuccessRate(pipeline, conclusion));

            Pipeline savedPipeline = pipelineRepository.save(pipeline);

            broadcastPipeline(savedPipeline);

            if ("FAILED".equals(pipelineStatus)) {
                sendFailureEmail(savedPipeline);
            }
        } catch (Exception e) {
            System.err.println("Error processing webhook payload asynchronously: " + e.getMessage());
        }
    }

    private double calculateSuccessRate(Pipeline pipeline, String conclusion) {
        double currentRate = pipeline.getSuccessRate();
        return "success".equals(conclusion) ? Math.min(currentRate + 0.05, 1.0) :
                "failure".equals(conclusion) ? Math.max(currentRate - 0.05, 0.0) : currentRate;
    }

    public void broadcastPipeline(Pipeline pipeline) {
        try {
            messagingTemplate.convertAndSend("/topic/pipelines", pipeline);
        } catch (Exception e) {
            System.err.println("Error broadcasting pipeline: " + e.getMessage());
        }
    }

    private void sendFailureEmail(Pipeline pipeline) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("admin@example.com");
            message.setSubject("Pipeline Failure Alert: " + pipeline.getName());
            message.setText(String.format(
                    "Pipeline %s failed at %s.\nLast Commit: %s\nSuccess Rate: %.2f%%",
                    pipeline.getName(), pipeline.getLastRunTime(), pipeline.getLastCommit(), pipeline.getSuccessRate() * 100
            ));
            message.setFrom("vinaybobby1122@gmail.com");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending failure email: " + e.getMessage());
        }
    }
}