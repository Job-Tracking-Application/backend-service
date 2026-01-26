package com.jobtracking.job.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.common.exception.ResourceNotFoundException;
import com.jobtracking.job.dto.JobWithSkillsResponse;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.profile.entity.Skill;
import com.jobtracking.profile.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public Job createJob(Job job, List<Long> skillIds) {
        // Save the job first
        Job savedJob = jobRepository.save(job);

        // Log job creation
        auditLogService.log("JOB", savedJob.getId(), "CREATED", savedJob.getRecruiterUserId(),
                "Created job: " + savedJob.getTitle());

        // Add skills if provided
        if (skillIds != null && !skillIds.isEmpty()) {
            try {
                List<Skill> skills = skillRepository.findAllById(skillIds);
                savedJob.setSkills(skills);
                savedJob = jobRepository.save(savedJob); // Save again to persist the relationship
            } catch (Exception e) {
                // Log the error but don't fail the job creation
                System.err.println("Error adding skills to job: " + e.getMessage());
            }
        }

        return savedJob;
    }

    @Override
    public Job getJobById(Long jobId) {
        return jobRepository.findByIdAndNotDeleted(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    @Override
    public JobWithSkillsResponse getJobWithSkillsById(Long jobId) {
        Job job = jobRepository.findByIdAndNotDeletedWithSkills(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return convertToJobWithSkillsResponse(job);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findByDeletedAtIsNull();
    }

    @Override
    public List<JobWithSkillsResponse> getAllJobsWithSkills() {
        List<Job> jobs = jobRepository.findByDeletedAtIsNullWithSkills();
        return jobs.stream()
                .map(this::convertToJobWithSkillsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Job> getJobsByRecruiter(Long recruiterId) {
        return jobRepository.findByRecruiterUserIdAndDeletedAtIsNull(recruiterId);
    }

    @Override
    public List<JobWithSkillsResponse> getJobsByRecruiterWithSkills(Long recruiterId) {
        List<Job> jobs = jobRepository.findByRecruiterUserIdAndDeletedAtIsNullWithSkills(recruiterId);
        return jobs.stream()
                .map(this::convertToJobWithSkillsResponse)
                .collect(Collectors.toList());
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

        // Update skills
        if (skillIds != null) {
            List<Skill> skills = skillRepository.findAllById(skillIds);
            existingJob.setSkills(skills);
        }

        Job updatedJob = jobRepository.save(existingJob);

        // Log job update
        auditLogService.log("JOB", updatedJob.getId(), "UPDATED", updatedJob.getRecruiterUserId(),
                "Updated job: " + updatedJob.getTitle());

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

        // Note: Skills relationship is maintained for soft delete
    }

    @Override
    @Transactional
    public void restoreJob(Long jobId) {
        // Find job including soft-deleted ones
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        // Restore the job
        job.setDeletedAt(null);
        job.setIsActive(true);

        jobRepository.save(job);
    }

    private JobWithSkillsResponse convertToJobWithSkillsResponse(Job job) {
        JobWithSkillsResponse response = new JobWithSkillsResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setLocation(job.getLocation());
        response.setMinSalary(job.getMinSalary());
        response.setMaxSalary(job.getMaxSalary());
        response.setMinExperience(job.getMinExperience());
        response.setMaxExperience(job.getMaxExperience());
        response.setJobType(job.getJobType());
        response.setCompanyId(job.getCompanyId());

        // Fetch and set company name
        if (job.getCompanyId() != null) {
            organizationRepository.findById(job.getCompanyId())
                    .ifPresent(org -> response.setCompanyName(org.getName()));
        }

        response.setRecruiterUserId(job.getRecruiterUserId());
        response.setIsActive(job.getIsActive());
        response.setPostedAt(job.getPostedAt());
        response.setDeadline(job.getDeadline());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());

        // Convert skills
        if (job.getSkills() != null) {
            List<JobWithSkillsResponse.SkillInfo> skillInfos = job.getSkills().stream()
                    .map(skill -> new JobWithSkillsResponse.SkillInfo(skill.getId(), skill.getName()))
                    .collect(Collectors.toList());
            response.setSkills(skillInfos);
        }

        return response;
    }
}
