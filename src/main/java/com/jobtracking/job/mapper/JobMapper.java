package com.jobtracking.job.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.jobtracking.common.mapper.BaseMapper;
import com.jobtracking.job.dto.JobWithSkillsResponse;
import com.jobtracking.job.entity.Job;
import com.jobtracking.profile.entity.Skill;

/**
 * Simple Job mapper - easy to understand for freshers
 * No complex patterns, just straightforward mapping
 */
@Component
public class JobMapper extends BaseMapper {

    /**
     * Convert Job entity to DTO
     */
    public JobWithSkillsResponse toDTO(Job job) {
        return toDTO(job, null);
    }

    /**
     * Convert Job entity to DTO with company name
     */
    public JobWithSkillsResponse toDTO(Job job, String companyName) {
        // Check if job is null
        if (job == null) {
            return null;
        }

        // Create new DTO object
        JobWithSkillsResponse dto = new JobWithSkillsResponse();
        
        // Copy all basic fields - simple field by field mapping
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setMinSalary(job.getMinSalary());
        dto.setMaxSalary(job.getMaxSalary());
        dto.setMinExperience(job.getMinExperience());
        dto.setMaxExperience(job.getMaxExperience());
        dto.setJobType(job.getJobType());
        dto.setCompanyId(job.getCompanyId());
        dto.setCompanyName(companyName);
        dto.setRecruiterUserId(job.getRecruiterUserId());
        dto.setIsActive(job.getIsActive());
        dto.setPostedAt(job.getPostedAt());
        dto.setDeadline(job.getDeadline());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setUpdatedAt(job.getUpdatedAt());
        
        // Map skills - simple loop approach
        dto.setSkills(mapSkills(job.getSkills()));
        
        return dto;
    }

    /**
     * Convert DTO to Job entity
     */
    public Job toEntity(JobWithSkillsResponse dto) {
        // Check if dto is null
        if (dto == null) {
            return null;
        }

        // Create new Job entity
        Job job = new Job();
        
        // Copy fields from DTO to entity
        job.setId(dto.getId());
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());
        job.setMinExperience(dto.getMinExperience());
        job.setMaxExperience(dto.getMaxExperience());
        job.setJobType(dto.getJobType());
        job.setCompanyId(dto.getCompanyId());
        job.setRecruiterUserId(dto.getRecruiterUserId());
        job.setIsActive(dto.getIsActive());
        job.setPostedAt(dto.getPostedAt());
        job.setDeadline(dto.getDeadline());
        
        return job;
    }

    /**
     * Update existing job with DTO data
     */
    public Job updateEntity(Job existingJob, JobWithSkillsResponse dto) {
        // Check if both are not null
        if (existingJob == null || dto == null) {
            return existingJob;
        }

        // Update only the fields that can be changed
        existingJob.setTitle(dto.getTitle());
        existingJob.setDescription(dto.getDescription());
        existingJob.setLocation(dto.getLocation());
        existingJob.setMinSalary(dto.getMinSalary());
        existingJob.setMaxSalary(dto.getMaxSalary());
        existingJob.setMinExperience(dto.getMinExperience());
        existingJob.setMaxExperience(dto.getMaxExperience());
        existingJob.setJobType(dto.getJobType());
        existingJob.setIsActive(dto.getIsActive());
        existingJob.setDeadline(dto.getDeadline());

        return existingJob;
    }

    /**
     * Convert list of jobs to list of DTOs
     */
    public List<JobWithSkillsResponse> toDTOList(List<Job> jobs) {
        // Check if list is null or empty
        if (jobs == null || jobs.isEmpty()) {
            return new ArrayList<>();
        }

        // Create result list
        List<JobWithSkillsResponse> dtoList = new ArrayList<>();
        
        // Loop through each job and convert
        for (Job job : jobs) {
            JobWithSkillsResponse dto = toDTO(job);
            if (dto != null) {
                dtoList.add(dto);
            }
        }
        
        return dtoList;
    }

    /**
     * Map skills from entity to DTO - simple approach
     */
    private List<JobWithSkillsResponse.SkillInfo> mapSkills(List<Skill> skills) {
        // Check if skills list is null or empty
        if (skills == null || skills.isEmpty()) {
            return new ArrayList<>();
        }

        // Create result list
        List<JobWithSkillsResponse.SkillInfo> skillInfoList = new ArrayList<>();
        
        // Loop through each skill and convert
        for (Skill skill : skills) {
            if (skill != null) {
                JobWithSkillsResponse.SkillInfo skillInfo = 
                    new JobWithSkillsResponse.SkillInfo(skill.getId(), skill.getName());
                skillInfoList.add(skillInfo);
            }
        }
        
        return skillInfoList;
    }
}