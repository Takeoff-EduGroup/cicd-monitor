package com.devopsboard.cicd_monitor.repository;

import com.devopsboard.cicd_monitor.entity.DeploymentEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDeploymentEventRepository extends JpaRepository<DeploymentEvent, Long> {
    Page<DeploymentEvent> findByRepoOrderByTimestampDesc(String repo, Pageable pageable);
    Page<DeploymentEvent> findAllByOrderByTimestampDesc(Pageable pageable);
}