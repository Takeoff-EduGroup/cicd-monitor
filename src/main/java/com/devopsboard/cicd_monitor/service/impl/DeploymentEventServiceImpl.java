package com.devopsboard.cicd_monitor.service.impl;

import com.devopsboard.cicd_monitor.dto.DeploymentEventDto;
import com.devopsboard.cicd_monitor.entity.DeploymentEvent;
import com.devopsboard.cicd_monitor.repository.IDeploymentEventRepository;
import com.devopsboard.cicd_monitor.service.IDeploymentEventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeploymentEventServiceImpl implements IDeploymentEventService {

    private final IDeploymentEventRepository repo;
    private final SimpMessagingTemplate messaging;

    public DeploymentEventServiceImpl(IDeploymentEventRepository repo,
                                      SimpMessagingTemplate messaging) {
        this.repo = repo;
        this.messaging = messaging;
    }

    @Override
    @Transactional
    public DeploymentEvent saveFromDto(DeploymentEventDto dto) {
        DeploymentEvent e = new DeploymentEvent();
        e.setRepo(dto.getRepo());
        e.setPipelineId(dto.getPipelineId());
        e.setStage(dto.getStage());
        e.setStatus(dto.getStatus());
        e.setMessage(dto.getMessage());
        e.setEnvironment(dto.getEnvironment());
        e.setTriggeredBy(dto.getTriggeredBy());
        // timestamp auto-set in entity
        return repo.save(e);
    }

    @Override
    public void broadcastEvent(DeploymentEvent event) {
        messaging.convertAndSend("/topic/deployments", event);
    }

    @Override
    public Page<DeploymentEvent> recent(Pageable pageable) {
        return repo.findAllByOrderByTimestampDesc(pageable);
    }
}
