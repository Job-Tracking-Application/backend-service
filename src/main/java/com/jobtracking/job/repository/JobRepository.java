package com.jobtracking.job.repository;

import org.springframework.data.jpa.repository. JpaRepository;
import com.jobtracking.job.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

}