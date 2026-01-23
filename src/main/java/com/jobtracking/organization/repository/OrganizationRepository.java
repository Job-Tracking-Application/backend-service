package com.jobtracking.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jobtracking.organization.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
