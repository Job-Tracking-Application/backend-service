package com.jobtracking.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.organization.entity.Organization;
import com.jobtracking.common.repository.BaseRepository;

@Repository
public interface OrganizationRepository extends BaseRepository<Organization> {
    
    boolean existsByRecruiterUserId(Long recruiterId);
    
    List<Organization> findByRecruiterUserId(Long recruiterId);
    
    Optional<Organization> findByName(String name);
    
    // Count verified organizations
    @Query("SELECT COUNT(o) FROM Organization o WHERE o.verified = true")
    long countByVerifiedTrue();
    
    // Count unverified organizations
    @Query("SELECT COUNT(o) FROM Organization o WHERE o.verified = false")
    long countByVerifiedFalse();
    
    // Find verified organizations
    @Query("SELECT o FROM Organization o WHERE o.verified = true ORDER BY o.createdAt DESC")
    List<Organization> findByVerifiedTrue();
    
    // Find unverified organizations
    @Query("SELECT o FROM Organization o WHERE o.verified = false ORDER BY o.createdAt DESC")
    List<Organization> findByVerifiedFalse();
    
    // Find organizations by city
    @Query("SELECT o FROM Organization o WHERE o.city = :city ORDER BY o.name ASC")
    List<Organization> findByCity(@Param("city") String city);
    
    // Search organizations by name (case insensitive)
    @Query("SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY o.name ASC")
    List<Organization> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Check if organization name exists (case insensitive)
    @Query("SELECT COUNT(o) > 0 FROM Organization o WHERE LOWER(o.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
}
