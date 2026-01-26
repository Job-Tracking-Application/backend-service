package com.jobtracking.dashboard.service;

import org.springframework.stereotype.Service;

import com.jobtracking.application.enums.ApplicationStatus;
import com.jobtracking.application.repository.ApplicationRepository;
import com.jobtracking.dashboard.dto.DashboardStatsResponse;
import com.jobtracking.job.repository.JobRepository;

@Service
public class DashboardService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public DashboardService(JobRepository jobRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public DashboardStatsResponse getRecruiterStats(Long recruiterId) {
        try {
            // Count active jobs for this recruiter (excluding soft-deleted)
            long activeJobs = 0;
            try {
                activeJobs = jobRepository.countByRecruiterUserIdAndIsActiveTrueAndDeletedAtIsNull(recruiterId);
            } catch (Exception e) {
                // Error counting active jobs - use default 0
            }
            
            // Count pending applications for recruiter's jobs
            long pendingApplications = 0;
            try {
                pendingApplications = applicationRepository.countApplicationsForRecruiterByStatus(recruiterId, ApplicationStatus.APPLIED);
            } catch (Exception e) {
                // Error counting pending applications - use default 0
            }
            
            // Count hired candidates for recruiter's jobs
            long hiredCandidates = 0;
            try {
                hiredCandidates = applicationRepository.countApplicationsForRecruiterByStatus(recruiterId, ApplicationStatus.HIRED);
            } catch (Exception e) {
                // Error counting hired candidates - use default 0
            }
            
            return new DashboardStatsResponse(activeJobs, pendingApplications, hiredCandidates);
        } catch (Exception e) {
            // Return zero stats if there's an error
            return new DashboardStatsResponse(0L, 0L, 0L);
        }
    }

    public DashboardStatsResponse getJobSeekerStats(Long jobSeekerId) {
        try {
            // Count total applications by this job seeker
            long totalApplications = applicationRepository.countByUserId(jobSeekerId);
            
            // Count pending applications (using APPLIED status)
            long pendingApplications = applicationRepository.countByUserIdAndStatus(jobSeekerId, ApplicationStatus.APPLIED);
            
            // Count accepted applications
            long acceptedApplications = applicationRepository.countByUserIdAndStatus(jobSeekerId, ApplicationStatus.HIRED);
            
            return new DashboardStatsResponse(totalApplications, pendingApplications, acceptedApplications);
        } catch (Exception e) {
            // Return zero stats if there's an error
            return new DashboardStatsResponse(0L, 0L, 0L);
        }
    }
}