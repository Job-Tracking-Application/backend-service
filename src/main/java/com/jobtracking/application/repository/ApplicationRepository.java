package com.jobtracking.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracking.application.entity.Application;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);

    List<Application> findByUserId(Long userId);

    boolean existsByJobIdAndUserId(Long jobId, Long userId);
}