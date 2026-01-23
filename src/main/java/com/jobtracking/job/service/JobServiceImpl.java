package com.jobtracking.job.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracking.job.entity.Job;
import com.jobtracking.job.entity.JobSkill;
import com.jobtracking.job.entity.JobSkillId;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.job.repository.JobSkillRepository;
import com.jobtracking.job.service.JobService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;

    @Override
    @Transactional
    public Job createJob(Job job, List<Long> skillIds) {
        Job savedJob = jobRepository.save(job);

        for (Long skillId : skillIds) {
            JobSkill jobSkill = new JobSkill();
            jobSkill.setId(new JobSkillId(savedJob.getId(), skillId));
            jobSkillRepository.save(jobSkill);
        }
        return savedJob;
    }

    @Override
    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    @Transactional
    public Job updateJob(Long jobId, Job job, List<Long> skillIds) {
        Job existingJob = getJobById(jobId);

        existingJob.setTitle(job.getTitle());
        existingJob.setDescription(job.getDescription());
        existingJob.setLocation(job.getLocation());
        existingJob.setMinSalary(job.getMinSalary());
        existingJob.setMaxSalary(job.getMaxSalary());
        existingJob.setJobType(job.getJobType());

        Job updatedJob = jobRepository.save(existingJob);

        // update skills
        jobSkillRepository.deleteByIdJobId(jobId);
        for (Long skillId : skillIds) {
            JobSkill jobSkill = new JobSkill();
            jobSkill.setId(new JobSkillId(jobId, skillId));
            jobSkillRepository.save(jobSkill);
        }

        return updatedJob;
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        jobSkillRepository.deleteByIdJobId(jobId);
        jobRepository.deleteById(jobId);
    }
}

