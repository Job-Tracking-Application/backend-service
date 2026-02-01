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
                return ResponseUtil.error("Recruiter profile not found. Please create a company first to set up your recruiter profile.");
            }

            if (recruiterProfile.getCompany() == null) {
                return ResponseUtil.error("No company associated with your profile. Please create or join a company before posting jobs.");
            }

            // Set recruiter and company from recruiter profile
            job.setRecruiter(recruiterProfile);
            job.setCompany(recruiterProfile.getCompany());

            // Check company verification status
            Long companyId = recruiterProfile.getCompany().getId();
            if (!recruiterProfile.getCompany().getVerified()) {
                return ResponseUtil.error("Your company '" + recruiterProfile.getCompany().getName() + "' is not yet verified. Please contact admin for company verification before posting jobs.");
            }

            // Use centralized authorization utility for ownership check
            if (!authorizationUtil.isRecruiterAuthorizedForOrganization(recruiterId, companyId)) {
                return ResponseUtil.forbidden("You are not authorized to post jobs for this company. Please ensure you own this company and it is verified.");
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
                return ResponseUtil.error("Recruiter profile not found. Please create a company first to set up your recruiter profile.");
            }

            if (recruiterProfile.getCompany() == null) {
                return ResponseUtil.error("No company associated with your profile. Please create or join a company before updating jobs.");
            }

            // Check company verification status
            Long companyId = recruiterProfile.getCompany().getId();
            if (!recruiterProfile.getCompany().getVerified()) {
                return ResponseUtil.error("Your company '" + recruiterProfile.getCompany().getName() + "' is not yet verified. Please contact admin for company verification before updating jobs.");
            }

            // Use centralized authorization utility for ownership check
            if (!authorizationUtil.isRecruiterAuthorizedForOrganization(recruiterId, companyId)) {
                return ResponseUtil.forbidden("You are not authorized to update jobs for this company. Please ensure you own this company and it is verified.");
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