package com.devopsboard.cicd_monitor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deployment_event",
        indexes = {
                @Index(name = "idx_deploy_timestamp", columnList = "timestamp"),
                @Index(name = "idx_deploy_pipeline", columnList = "pipeline_id")
        })
public class DeploymentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repo", nullable = false)
    private String repo;

    @Column(name = "pipeline_id")
    private String pipelineId;

    @Column(name = "stage")
    private String stage; // build/test/deploy

    @Column(name = "status")
    private String status; // started, success, failed

    @Lob
    @Column(name = "message")
    private String message;

    @Column(name = "environment")
    private String environment; // dev/staging/prod

    @Column(name = "triggered_by")
    private String triggeredBy;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp = Instant.now();

}