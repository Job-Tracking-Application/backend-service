package com.jobtracking.job.service;

import java.util.List;

import com.jobtracking.job.dto.JobWithSkillsResponse;
import com.jobtracking.job.entity.Job;

public interface JobService {

    Job createJob(Job job, List<Long> skillIds);

    Job getJobById(Long jobId);
    
    JobWithSkillsResponse getJobWithSkillsById(Long jobId);

    List<Job> getAllJobs();
    
    List<JobWithSkillsResponse> getAllJobsWithSkills();

    List<Job> getJobsByRecruiter(Long recruiterId);
    
    List<JobWithSkillsResponse> getJobsByRecruiterWithSkills(Long recruiterId);

    Job updateJob(Long jobId, Job job, List<Long> skillIds);

    void deleteJob(Long jobId);
    
    void restoreJob(Long jobId);
}
