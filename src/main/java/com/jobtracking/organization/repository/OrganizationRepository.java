package com.jobtracking.organization.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobtracking.organization.entity.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    Optional<Organization> findByRecruiterUserId(Long recruiterUserId);
    
    boolean existsByRecruiterUserId(Long recruiterUserId);
}
