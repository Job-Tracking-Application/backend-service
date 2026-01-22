package com.jobtracking.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.application.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    @Query("""
       SELECT a FROM Application a
       WHERE (:status IS NULL OR a.status = :status)
    """)
    Page<Application> filterApplications(
            @Param("status") String status,
            Pageable pageable
    );
}