package com.jobtracking.job.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.job.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    // Count active jobs for a recruiter (excluding soft-deleted)
    long countByRecruiterUserIdAndIsActiveTrueAndDeletedAtIsNull(Long recruiterId);
    
    // Find jobs by recruiter (excluding soft-deleted) - Latest first
    @Query("SELECT j FROM Job j WHERE j.recruiterUserId = :recruiterId AND j.deletedAt IS NULL ORDER BY j.postedAt DESC, j.createdAt DESC")
    List<Job> findByRecruiterUserIdAndDeletedAtIsNull(@Param("recruiterId") Long recruiterId);
    
    // Find jobs by recruiter with skills (excluding soft-deleted) - Latest first
    @EntityGraph(attributePaths = {"skills"})
    @Query("SELECT j FROM Job j WHERE j.recruiterUserId = :recruiterId AND j.deletedAt IS NULL ORDER BY j.postedAt DESC, j.createdAt DESC")
    List<Job> findByRecruiterUserIdAndDeletedAtIsNullWithSkills(@Param("recruiterId") Long recruiterId);
    
    // Find all active jobs (excluding soft-deleted) - Latest first
    @Query("SELECT j FROM Job j WHERE j.deletedAt IS NULL ORDER BY j.postedAt DESC, j.createdAt DESC")
    List<Job> findByDeletedAtIsNull();
    
    // Find all active jobs with skills (excluding soft-deleted) - Latest first
    @EntityGraph(attributePaths = {"skills"})
    @Query("SELECT j FROM Job j WHERE j.deletedAt IS NULL ORDER BY j.postedAt DESC, j.createdAt DESC")
    List<Job> findByDeletedAtIsNullWithSkills();
    
    // Find job by ID (excluding soft-deleted)
    @Query("SELECT j FROM Job j WHERE j.id = :id AND j.deletedAt IS NULL")
    Optional<Job> findByIdAndNotDeleted(@Param("id") Long id);
    
    // Find job by ID with skills (excluding soft-deleted)
    @EntityGraph(attributePaths = {"skills"})
    @Query("SELECT j FROM Job j WHERE j.id = :id AND j.deletedAt IS NULL")
    Optional<Job> findByIdAndNotDeletedWithSkills(@Param("id") Long id);
}