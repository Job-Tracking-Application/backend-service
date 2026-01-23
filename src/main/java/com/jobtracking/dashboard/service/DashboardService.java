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
            long activeJobs = jobRepository.countByRecruiterUserIdAndIsActiveTrueAndDeletedAtIsNull(recruiterId);
            
            // Count pending applications for recruiter's jobs
            long pendingApplications = applicationRepository.countPendingApplicationsForRecruiter(recruiterId);
            
            // Count hired candidates for recruiter's jobs
            long hiredCandidates = applicationRepository.countHiredApplicationsForRecruiter(recruiterId);
            
            return new DashboardStatsResponse(activeJobs, pendingApplications, hiredCandidates);
        } catch (Exception e) {
            // Return zero stats if there's an error
            System.err.println("Error calculating recruiter stats: " + e.getMessage());
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
            System.err.println("Error calculating job seeker stats: " + e.getMessage());
            return new DashboardStatsResponse(0L, 0L, 0L);
        }
    }
}