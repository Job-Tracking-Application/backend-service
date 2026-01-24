package com.jobtracking.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jobtracking.organization.entity.Organization;
import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    boolean existsByRecruiterUserId(Long recruiterId);
    
    List<Organization> findByRecruiterUserId(Long recruiterId);
}
