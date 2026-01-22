package com.jobtracking.profile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracking.profile.entity.JobSeekerProfile;
import com.jobtracking.profile.entity.JobSeekerSkill;

public interface JobSeekerSkillsRepository extends JpaRepository<JobSeekerSkill,Long>{

	List<JobSeekerSkill> findByJobSeekerProfile(JobSeekerProfile jobSeekerProfile);
	
}
