package com.jobtracking.job.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobtracking.job.entity.Job;
import com.jobtracking.job.service.JobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<ApiResponse<Job>> createJob(
            @RequestBody Job job,
            @RequestParam List<Long> skillIds) {

        Job savedJob = jobService.createJob(job, skillIds);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Job created successfully", savedJob));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> getJob(@PathVariable Long id) {
        Job job = jobService.getJobById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Job fetched successfully", job)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Job>>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Jobs fetched successfully", jobs)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> updateJob(
            @PathVariable Long id,
            @RequestBody Job job,
            @RequestParam List<Long> skillIds) {

        Job updatedJob = jobService.updateJob(id, job, skillIds);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Job updated successfully", updatedJob)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Job deleted successfully", null)
        );
    }
}
