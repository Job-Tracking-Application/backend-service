package com.jobtracking.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.profile.entity.RecruiterProfile;
import com.jobtracking.common.repository.BaseRepository;

@Repository
public interface RecruiterProfileRepository extends BaseRepository<RecruiterProfile> {
    
    Optional<RecruiterProfile> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    // Find verified recruiters
    @Query("SELECT r FROM RecruiterProfile r WHERE r.verified = true ORDER BY r.updatedAt DESC")
    List<RecruiterProfile> findByVerifiedTrue();
    
    // Find unverified recruiters
    @Query("SELECT r FROM RecruiterProfile r WHERE r.verified = false ORDER BY r.updatedAt DESC")
    List<RecruiterProfile> findByVerifiedFalse();
    
    // Count verified recruiters
    @Query("SELECT COUNT(r) FROM RecruiterProfile r WHERE r.verified = true")
    long countByVerifiedTrue();
    
    // Count unverified recruiters
    @Query("SELECT COUNT(r) FROM RecruiterProfile r WHERE r.verified = false")
    long countByVerifiedFalse();
    
    // Find recruiters by company
    @Query("SELECT r FROM RecruiterProfile r WHERE r.company.id = :companyId ORDER BY r.updatedAt DESC")
    List<RecruiterProfile> findByCompanyId(@Param("companyId") Long companyId);
    
    // Find recruiters by specialization
    @Query("SELECT r FROM RecruiterProfile r WHERE LOWER(r.specialization) LIKE LOWER(CONCAT('%', :specialization, '%')) ORDER BY r.updatedAt DESC")
    List<RecruiterProfile> findBySpecializationContaining(@Param("specialization") String specialization);
    
    // Find recruiters by years of experience range
    @Query("SELECT r FROM RecruiterProfile r WHERE r.yearsExperience >= :minYears AND r.yearsExperience <= :maxYears ORDER BY r.yearsExperience DESC")
    List<RecruiterProfile> findByYearsExperienceBetween(@Param("minYears") Integer minYears, @Param("maxYears") Integer maxYears);
}
