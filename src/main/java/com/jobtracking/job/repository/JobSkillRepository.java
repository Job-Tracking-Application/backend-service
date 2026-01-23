package com.jobtracking.job.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobtracking.job.entity.JobSkillId;
import com.jobtracking.job.entity.JobSkill;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {

    List<JobSkill> findByIdJobId(Long jobId);

    void deleteByIdJobId(Long jobId);
}
