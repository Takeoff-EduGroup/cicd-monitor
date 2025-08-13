package com.devopsboard.cicd_monitor.service;

import com.devopsboard.cicd_monitor.dto.DeploymentEventDto;
import com.devopsboard.cicd_monitor.entity.DeploymentEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface IDeploymentEventService {
    @Transactional
    DeploymentEvent saveFromDto(DeploymentEventDto dto);
    void broadcastEvent(DeploymentEvent event);
    Page<DeploymentEvent> recent(Pageable pageable);
}
