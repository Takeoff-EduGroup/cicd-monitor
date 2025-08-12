package com.devopsboard.cicd_monitor.repository;

import com.devopsboard.cicd_monitor.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPipelineRepository extends JpaRepository<Pipeline,Long> {
    Optional<Pipeline> findByName(String name);
}
