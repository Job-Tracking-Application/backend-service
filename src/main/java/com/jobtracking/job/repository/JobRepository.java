package com.jobtracking.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.job.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    // Count active jobs for a recruiter (excluding soft-deleted)
    long countByRecruiterUserIdAndIsActiveTrueAndDeletedAtIsNull(Long recruiterId);
    
    // Find jobs by recruiter (excluding soft-deleted)
    java.util.List<Job> findByRecruiterUserIdAndDeletedAtIsNull(Long recruiterId);
    
    // Find all active jobs (excluding soft-deleted)
    java.util.List<Job> findByDeletedAtIsNull();
    
    // Find job by ID (excluding soft-deleted)
    @Query("SELECT j FROM Job j WHERE j.id = :id AND j.deletedAt IS NULL")
    java.util.Optional<Job> findByIdAndNotDeleted(@Param("id") Long id);
}