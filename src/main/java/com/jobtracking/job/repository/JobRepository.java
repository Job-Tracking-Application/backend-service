package com.jobtracking.job.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.job.entity.Job;
import com.jobtracking.common.repository.SoftDeleteRepository;

@Repository
public interface JobRepository extends SoftDeleteRepository<Job> {
    
    // Count active jobs for a recruiter (excluding soft-deleted)
    @Query("SELECT COUNT(j) FROM Job j WHERE j.recruiter.user.id = :recruiterId AND j.isActive = true AND j.deletedAt IS NULL")
    long countByRecruiterIdAndIsActiveTrueAndDeletedAtIsNull(@Param("recruiterId") Long recruiterId);
    
    // Find jobs by recruiter (excluding soft-deleted) - Latest first
    @Query("SELECT j FROM Job j WHERE j.recruiter.user.id = :recruiterId AND j.deletedAt IS NULL ORDER BY j.postedAt DESC, j.createdAt DESC")
    List<Job> findByRecruiterIdAndDeletedAtIsNull(@Param("recruiterId") Long recruiterId);
    
    // Find jobs by recruiter with skills (excluding soft-deleted) - Latest first
    @EntityGraph(attributePaths = {"skills"})
    @Query("SELECT j FROM Job j WHERE j.recruiter.user.id = :recruiterId AND j.deletedAt IS NULL ORDER BY j.postedAt DESC, j.createdAt DESC")
    List<Job> findByRecruiterIdAndDeletedAtIsNullWithSkills(@Param("recruiterId") Long recruiterId);
    
    // Find all active jobs (excluding soft-deleted) - Latest first
    @Query("SELECT j FROM Job j WHERE j.deletedAt IS NULL ORDER BY j.postedAt DESC, j.createdAt DESC")
    List<Job> findByDeletedAtIsNull();
    
    // Find all active jobs with skills (excluding soft-deleted) - Latest first
    @EntityGraph(attributePaths = {"skills"})
    @Query("SELECT j FROM Job j WHERE j.deletedAt IS NULL ORDER BY j.postedAt DESC, j.createdAt DESC")
    List<Job> findByDeletedAtIsNullWithSkills();
    
    // Find job by ID with skills (excluding soft-deleted)
    @EntityGraph(attributePaths = {"skills"})
    @Query("SELECT j FROM Job j WHERE j.id = :id AND j.deletedAt IS NULL")
    Optional<Job> findByIdAndNotDeletedWithSkills(@Param("id") Long id);
    
    // Count active jobs (excluding soft-deleted)
    @Query("SELECT COUNT(j) FROM Job j WHERE j.isActive = true AND j.deletedAt IS NULL")
    long countByIsActiveTrueAndDeletedAtIsNull();
    
    // Find jobs by company ID (excluding soft-deleted)
    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.deletedAt IS NULL ORDER BY j.postedAt DESC")
    List<Job> findByCompanyIdAndDeletedAtIsNull(@Param("companyId") Long companyId);
    
    // Count jobs by company ID (excluding soft-deleted)
    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId AND j.deletedAt IS NULL")
    long countByCompanyIdAndDeletedAtIsNull(@Param("companyId") Long companyId);
}