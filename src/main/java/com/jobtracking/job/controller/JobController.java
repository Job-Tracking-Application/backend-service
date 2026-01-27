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

@RestController
@RequestMapping("/jobs")
public class JobController extends BaseController {

    private final JobService jobService;
    private final AuthorizationUtil authorizationUtil;

    public JobController(JobService jobService, AuthorizationUtil authorizationUtil) {
        this.jobService = jobService;
        this.authorizationUtil = authorizationUtil;
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

            // Use centralized authorization utility
            if (!authorizationUtil.isRecruiterAuthorizedForOrganization(recruiterId, job.getCompanyId())) {
                return ResponseUtil.forbidden("Only verified recruiters from verified organizations can create jobs");
            }

            job.setRecruiterUserId(recruiterId);

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

            // Use centralized authorization utility
            if (!authorizationUtil.isRecruiterAuthorizedForOrganization(recruiterId, job.getCompanyId())) {
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