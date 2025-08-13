package com.devopsboard.cicd_monitor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentEventDto {
    @NotBlank
    private String repo;
    private String pipelineId;
    private String stage;
    private String status;
    private String message;
    private String environment;
    private String triggeredBy;

}