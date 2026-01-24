package com.jobtracking.job.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracking.audit.service.AuditLogService;
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
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public Job createJob(Job job, List<Long> skillIds) {
        // Save the job first
        Job savedJob = jobRepository.save(job);
        
        // Log job creation
        auditLogService.log("JOB", savedJob.getId(), "CREATED", savedJob.getRecruiterUserId(), 
            "Created job: " + savedJob.getTitle());

        // Only process skills if skillIds is not empty and not null
        if (skillIds != null && !skillIds.isEmpty()) {
            try {
                for (Long skillId : skillIds) {
                    JobSkill jobSkill = new JobSkill();
                    jobSkill.setId(new JobSkillId(savedJob.getId(), skillId));
                    jobSkillRepository.save(jobSkill);
                }
            } catch (Exception e) {
                // Log the error but don't fail the job creation
                // Skill creation failed, but job creation succeeded
            }
        }
        
        return savedJob;
    }

    @Override
    public Job getJobById(Long jobId) {
        return jobRepository.findByIdAndNotDeleted(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findByDeletedAtIsNull();
    }

    @Override
    public List<Job> getJobsByRecruiter(Long recruiterId) {
        return jobRepository.findByRecruiterUserIdAndDeletedAtIsNull(recruiterId);
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
        
        // Log job update
        auditLogService.log("JOB", updatedJob.getId(), "UPDATED", updatedJob.getRecruiterUserId(), 
            "Updated job: " + updatedJob.getTitle());

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
        Job job = getJobById(jobId);
        
        // Soft delete: set deletedAt timestamp instead of actually deleting
        job.setDeletedAt(LocalDateTime.now());
        job.setIsActive(false); // Also mark as inactive
        
        jobRepository.save(job);
        
        // Log job deletion (soft delete)
        auditLogService.log("JOB", jobId, "DELETED", job.getRecruiterUserId(), 
            "Soft deleted job: " + job.getTitle());
        
        // Note: We don't delete job skills for soft delete to maintain data integrity
        // jobSkillRepository.deleteByIdJobId(jobId);
    }

    @Override
    @Transactional
    public void restoreJob(Long jobId) {
        // Find job including soft-deleted ones
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Restore the job
        job.setDeletedAt(null);
        job.setIsActive(true);
        
        jobRepository.save(job);
    }
}

