package com.devopsboard.cicd_monitor.controller;

import com.devopsboard.cicd_monitor.entity.Pipeline;
import com.devopsboard.cicd_monitor.repository.IPipelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class PipelineController {
    @Autowired
    private IPipelineRepository pipelineRepository;

    @GetMapping("/pipelines")
    public List<Pipeline> getAllPipelines() {
        return pipelineRepository.findAll();
    }

    @GetMapping("/{id}")
    public Pipeline getPipelineById(@PathVariable Long id) {
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pipeline not found"));
    }

    @GetMapping("/{id}/logs")
    public List<Map<String, String>> getPipelineLogs(@PathVariable Long id) {
        // Mock logs for demonstration
        return List.of(
                Map.of("timestamp", LocalDateTime.now().toString(), "message", "Build started"),
                Map.of("timestamp", LocalDateTime.now().minusMinutes(1).toString(), "message", "Tests passed"),
                Map.of("timestamp", LocalDateTime.now().minusMinutes(2).toString(), "message", "Deployed to production")
        );
    }
}