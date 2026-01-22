package com.jobtracking.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracking.profile.entity.JobSeekerProfile;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile,Long> {
	Optional<JobSeekerProfile> findByUserId(Long id);
}
