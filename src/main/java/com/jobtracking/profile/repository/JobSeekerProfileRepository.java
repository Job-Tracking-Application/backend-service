package com.jobtracking.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.profile.entity.JobSeekerProfile;
import com.jobtracking.common.repository.BaseRepository;

@Repository
public interface JobSeekerProfileRepository extends BaseRepository<JobSeekerProfile> {
    
    Optional<JobSeekerProfile> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    // Find profiles with resume links
    @Query("SELECT j FROM JobSeekerProfile j WHERE j.resumeLink IS NOT NULL ORDER BY j.updatedAt DESC")
    List<JobSeekerProfile> findAllWithResumeLink();
    
    // Find profiles by education keyword
    @Query("SELECT j FROM JobSeekerProfile j WHERE LOWER(j.education) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY j.updatedAt DESC")
    List<JobSeekerProfile> findByEducationContaining(@Param("keyword") String keyword);
    
    // Find profiles by experience keyword
    @Query("SELECT j FROM JobSeekerProfile j WHERE LOWER(j.experience) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY j.updatedAt DESC")
    List<JobSeekerProfile> findByExperienceContaining(@Param("keyword") String keyword);
    
    // Count profiles with resume links
    @Query("SELECT COUNT(j) FROM JobSeekerProfile j WHERE j.resumeLink IS NOT NULL")
    long countWithResumeLink();
}
