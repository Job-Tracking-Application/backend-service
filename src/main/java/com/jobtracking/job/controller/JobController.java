package com.jobtracking.job.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobtracking.common.controller.BaseController;
import com.jobtracking.common.response.ApiResponse;
import com.jobtracking.common.utils.AuthorizationUtil;
import com.jobtracking.common.utils.ResponseUtil;
import com.jobtracking.job.dto.JobWithSkillsResponse;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.service.JobService;
import com.jobtracking.profile.entity.RecruiterProfile;
import com.jobtracking.profile.repository.RecruiterProfileRepository;

@RestController
@RequestMapping("/jobs")
public class JobController extends BaseController {

    private final JobService jobService;
    private final AuthorizationUtil authorizationUtil;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public JobController(JobService jobService, AuthorizationUtil authorizationUtil, 
                        RecruiterProfileRepository recruiterProfileRepository) {
        this.jobService = jobService;
        this.authorizationUtil = authorizationUtil;
        this.recruiterProfileRepository = recruiterProfileRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobWithSkillsResponse>> createJob(
            @RequestBody Job job,
            @RequestParam(required = false) List<Long> skillIds) {

        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseUtil.unauthorized("User not authenticated");
            }

            // Get recruiter profile
            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiterId)
                .orElse(null);

            if (recruiterProfile == null) {
                return ResponseUtil.error("Please create a company profile first before posting jobs");
            }

            if (recruiterProfile.getCompany() == null) {
                return ResponseUtil.error("Please associate your profile with a company before posting jobs");
            }

            // Set recruiter and company from recruiter profile
            job.setRecruiter(recruiterProfile);
            job.setCompany(recruiterProfile.getCompany());

            // Use centralized authorization utility
            Long companyId = recruiterProfile.getCompany().getId();
            if (!authorizationUtil.isRecruiterAuthorizedForOrganization(recruiterId, companyId)) {
                return ResponseUtil.forbidden("Only verified recruiters from verified organizations can create jobs");
            }

            if (skillIds == null) {
                skillIds = List.of();
            }

            Job savedJob = jobService.createJob(job, skillIds);
            JobWithSkillsResponse response = jobService.getJobWithSkillsById(savedJob.getId());

            return ResponseUtil.success(response, "Job created successfully", HttpStatus.CREATED);
                    
        } catch (Exception e) {
            return ResponseUtil.internalError("Error creating job: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobWithSkillsResponse>> updateJob(
            @PathVariable Long id,
            @RequestBody Job job,
            @RequestParam(required = false) List<Long> skillIds) {

        try {
            Long recruiterId = getCurrentUserId();
            if (recruiterId == null) {
                return ResponseUtil.unauthorized("User not authenticated");
            }

            // Get recruiter profile
            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiterId)
                .orElse(null);

            if (recruiterProfile == null) {
                return ResponseUtil.error("Please create a company profile first before updating jobs");
            }

            if (recruiterProfile.getCompany() == null) {
                return ResponseUtil.error("Please associate your profile with a company before updating jobs");
            }

            // Use centralized authorization utility
            Long companyId = recruiterProfile.getCompany().getId();
            if (!authorizationUtil.isRecruiterAuthorizedForOrganization(recruiterId, companyId)) {
                return ResponseUtil.forbidden("Only verified recruiters from verified organizations can update jobs");
            }

            if (skillIds == null) {
                skillIds = List.of();
            }

            Job updatedJob = jobService.updateJob(id, job, skillIds);
            JobWithSkillsResponse response = jobService.getJobWithSkillsById(updatedJob.getId());

            return ResponseUtil.success(response, "Job updated successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("Error updating job: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobWithSkillsResponse>>> getAllJobs() {
        try {
            List<JobWithSkillsResponse> jobs = jobService.getAllJobsWithSkills();
            return ResponseUtil.success(jobs, "Jobs fetched successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("Error fetching jobs: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobWithSkillsResponse>> getJob(@PathVariable Long id) {
        try {
            JobWithSkillsResponse job = jobService.getJobWithSkillsById(id);
            return ResponseUtil.success(job, "Job fetched successfully");
        } catch (Exception e) {
            return ResponseUtil.notFound("Job not found: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseUtil.success(null, "Job deleted successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("Error deleting job: " + e.getMessage());
        }
    }
}