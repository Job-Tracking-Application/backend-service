package com.jobtracking.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.application.entity.Application;
import com.jobtracking.application.enums.ApplicationStatus;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    @Query("""
       SELECT a FROM Application a
       WHERE (:status IS NULL OR a.status = :status)
    """)
    Page<Application> filterApplications(
            @Param("status") ApplicationStatus status,
            Pageable pageable
    );
    
    // Methods from profile backend API
    List<Application> findByJobId(Long jobId);
    
    @Query("SELECT a FROM Application a WHERE a.user.id = :userId ORDER BY a.appliedAt DESC")
    List<Application> findByUserId(@Param("userId") Long userId);
    
    boolean existsByJobIdAndUserId(Long jobId, Long userId);
    
    // Dashboard stats methods
    long countByUserId(Long jobSeekerId);
    long countByUserIdAndStatus(Long jobSeekerId, ApplicationStatus status);
    
    @Query("""
        SELECT COUNT(a) FROM Application a 
        JOIN a.job j 
        WHERE j.recruiterUserId = :recruiterId AND a.status = :status
    """)
    long countApplicationsForRecruiterByStatus(@Param("recruiterId") Long recruiterId, @Param("status") ApplicationStatus status);
}