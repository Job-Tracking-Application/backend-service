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
            System.out.println("DashboardService: Getting stats for recruiter ID: " + recruiterId);
            
            // Count active jobs for this recruiter (excluding soft-deleted)
            long activeJobs = 0;
            try {
                activeJobs = jobRepository.countByRecruiterUserIdAndIsActiveTrueAndDeletedAtIsNull(recruiterId);
                System.out.println("DashboardService: Active jobs count: " + activeJobs);
            } catch (Exception e) {
                System.err.println("Error counting active jobs: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Count pending applications for recruiter's jobs
            long pendingApplications = 0;
            try {
                pendingApplications = applicationRepository.countApplicationsForRecruiterByStatus(recruiterId, ApplicationStatus.APPLIED);
                System.out.println("DashboardService: Pending applications count: " + pendingApplications);
            } catch (Exception e) {
                System.err.println("Error counting pending applications: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Count hired candidates for recruiter's jobs
            long hiredCandidates = 0;
            try {
                hiredCandidates = applicationRepository.countApplicationsForRecruiterByStatus(recruiterId, ApplicationStatus.HIRED);
                System.out.println("DashboardService: Hired candidates count: " + hiredCandidates);
            } catch (Exception e) {
                System.err.println("Error counting hired candidates: " + e.getMessage());
                e.printStackTrace();
            }
            
            DashboardStatsResponse response = new DashboardStatsResponse(activeJobs, pendingApplications, hiredCandidates);
            System.out.println("DashboardService: Returning response: " + response);
            return response;
        } catch (Exception e) {
            // Return zero stats if there's an error
            System.err.println("Error calculating recruiter stats: " + e.getMessage());
            e.printStackTrace();
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