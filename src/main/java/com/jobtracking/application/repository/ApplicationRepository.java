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
       WHERE a.deletedAt IS NULL 
       AND (:status IS NULL OR a.status = :status)
    """)
    Page<Application> filterApplications(
            @Param("status") ApplicationStatus status,
            Pageable pageable
    );
    
    // Methods from profile backend API
    @Query("SELECT a FROM Application a WHERE a.job.id = :jobId AND a.deletedAt IS NULL")
    List<Application> findByJobId(@Param("jobId") Long jobId);
    
    @Query("SELECT a FROM Application a WHERE a.user.id = :userId AND a.deletedAt IS NULL ORDER BY a.appliedAt DESC")
    List<Application> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(a) > 0 FROM Application a WHERE a.job.id = :jobId AND a.user.id = :userId AND a.deletedAt IS NULL")
    boolean existsByJobIdAndUserId(@Param("jobId") Long jobId, @Param("userId") Long userId);
    
    // Dashboard stats methods
    @Query("SELECT COUNT(a) FROM Application a WHERE a.user.id = :jobSeekerId AND a.deletedAt IS NULL")
    long countByUserId(@Param("jobSeekerId") Long jobSeekerId);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.user.id = :jobSeekerId AND a.status = :status AND a.deletedAt IS NULL")
    long countByUserIdAndStatus(@Param("jobSeekerId") Long jobSeekerId, @Param("status") ApplicationStatus status);
    
    @Query("""
        SELECT COUNT(a) FROM Application a 
        JOIN a.job j 
        WHERE j.recruiterUserId = :recruiterId AND a.status = :status AND a.deletedAt IS NULL
    """)
    long countApplicationsForRecruiterByStatus(@Param("recruiterId") Long recruiterId, @Param("status") ApplicationStatus status);
}