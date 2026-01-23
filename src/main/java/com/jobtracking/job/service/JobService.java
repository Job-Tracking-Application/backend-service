package com.jobtracking.job.service;

import java.util.List;

import com.jobtracking.job.entity.Job;

public interface JobService {

    Job createJob(Job job, List<Long> skillIds);

    Job getJobById(Long jobId);

    List<Job> getAllJobs();

    Job updateJob(Long jobId, Job job, List<Long> skillIds);

    void deleteJob(Long jobId);
}
