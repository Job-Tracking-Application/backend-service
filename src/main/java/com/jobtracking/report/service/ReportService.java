package com.jobtracking.report.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jobtracking.application.entity.Application;
import com.jobtracking.application.enums.ApplicationStatus;
import com.jobtracking.application.repository.ApplicationRepository;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;

@Service
public class ReportService {
    
    private final UserRepository userRepo;
    private final JobRepository jobRepo;
    private final OrganizationRepository orgRepo;
    private final ApplicationRepository appRepo;
    
    public ReportService(UserRepository userRepo, JobRepository jobRepo, 
                        OrganizationRepository orgRepo, ApplicationRepository appRepo) {
        this.userRepo = userRepo;
        this.jobRepo = jobRepo;
        this.orgRepo = orgRepo;
        this.appRepo = appRepo;
    }
    
    public Map<String, Object> getSummaryReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            // Basic counts using existing methods
            List<User> allUsers = userRepo.findAll();
            List<Job> allJobs = jobRepo.findAll();
            List<Organization> allCompanies = orgRepo.findAll();
            List<Application> allApplications = appRepo.findAll();
            
            long totalUsers = allUsers.size();
            long totalJobs = allJobs.stream().filter(j -> j.getDeletedAt() == null).count();
            long totalCompanies = allCompanies.size();
            long totalApplications = allApplications.size();
            
            // Active vs Inactive breakdown using streams
            long activeUsers = allUsers.stream().filter(u -> u.getActive()).count();
            long inactiveUsers = totalUsers - activeUsers;
            
            long activeJobs = allJobs.stream().filter(j -> j.getDeletedAt() == null && j.getIsActive()).count();
            long inactiveJobs = allJobs.stream().filter(j -> j.getDeletedAt() == null && !j.getIsActive()).count();
            
            long verifiedCompanies = allCompanies.stream().filter(c -> c.getVerified()).count();
            long unverifiedCompanies = totalCompanies - verifiedCompanies;
            
            // Application status breakdown
            Map<String, Long> applicationsByStatus = new HashMap<>();
            for (ApplicationStatus status : ApplicationStatus.values()) {
                long count = allApplications.stream().filter(a -> a.getStatus() == status).count();
                applicationsByStatus.put(status.name(), count);
            }
            
            // Role distribution
            Map<String, Long> usersByRole = new HashMap<>();
            usersByRole.put("ADMIN", allUsers.stream().filter(u -> u.getRoleId() == 1).count());
            usersByRole.put("RECRUITER", allUsers.stream().filter(u -> u.getRoleId() == 2).count());
            usersByRole.put("JOB_SEEKER", allUsers.stream().filter(u -> u.getRoleId() == 3).count());
            
            // Build report
            report.put("overview", Map.of(
                "totalUsers", totalUsers,
                "totalJobs", totalJobs,
                "totalCompanies", totalCompanies,
                "totalApplications", totalApplications
            ));
            
            report.put("userBreakdown", Map.of(
                "active", activeUsers,
                "inactive", inactiveUsers,
                "byRole", usersByRole
            ));
            
            report.put("jobBreakdown", Map.of(
                "active", activeJobs,
                "inactive", inactiveJobs
            ));
            
            report.put("companyBreakdown", Map.of(
                "verified", verifiedCompanies,
                "unverified", unverifiedCompanies
            ));
            
            report.put("applicationBreakdown", applicationsByStatus);
            
            report.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            report.put("reportType", "SUMMARY");
            
        } catch (Exception e) {
            // Fallback data in case of error
            report.put("error", "Failed to generate report: " + e.getMessage());
            report.put("overview", Map.of(
                "totalUsers", 0L,
                "totalJobs", 0L,
                "totalCompanies", 0L,
                "totalApplications", 0L
            ));
        }
        
        return report;
    }
    
    public Map<String, Object> getMatrixReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            // Get all data using existing methods
            List<Organization> companies = orgRepo.findAll();
            List<Job> jobs = jobRepo.findAll().stream()
                .filter(job -> job.getDeletedAt() == null)
                .collect(Collectors.toList());
            List<Application> applications = appRepo.findAll();
            
            // Company matrix using streams
            List<Map<String, Object>> companyMatrix = companies.stream().map(company -> {
                long jobCount = jobs.stream().filter(j -> j.getCompanyId().equals(company.getId())).count();
                long applicationCount = applications.stream()
                    .filter(a -> jobs.stream()
                        .anyMatch(j -> j.getId().equals(a.getJob().getId()) && j.getCompanyId().equals(company.getId())))
                    .count();
                
                Map<String, Object> companyData = new HashMap<>();
                companyData.put("id", company.getId());
                companyData.put("name", company.getName());
                companyData.put("city", company.getCity());
                companyData.put("verified", company.getVerified());
                companyData.put("jobCount", jobCount);
                companyData.put("applicationCount", applicationCount);
                companyData.put("avgApplicationsPerJob", jobCount > 0 ? (double) applicationCount / jobCount : 0.0);
                
                return companyData;
            }).collect(Collectors.toList());
            
            // Job matrix using streams
            List<Map<String, Object>> jobMatrix = jobs.stream().map(job -> {
                long applicationCount = applications.stream()
                    .filter(a -> a.getJob().getId().equals(job.getId()))
                    .count();
                
                Map<String, Object> jobData = new HashMap<>();
                jobData.put("id", job.getId());
                jobData.put("title", job.getTitle());
                jobData.put("companyId", job.getCompanyId());
                jobData.put("isActive", job.getIsActive());
                jobData.put("applicationCount", applicationCount);
                jobData.put("createdAt", job.getCreatedAt());
                
                return jobData;
            }).collect(Collectors.toList());
            
            // Calculate system-wide metrics
            double avgApplicationsPerJob = jobs.isEmpty() ? 0.0 : 
                (double) applications.size() / jobs.size();
            double avgJobsPerCompany = companies.isEmpty() ? 0.0 : 
                (double) jobs.size() / companies.size();
            
            long activeJobs = jobs.stream().filter(j -> j.getIsActive()).count();
            long inactiveJobs = jobs.stream().filter(j -> !j.getIsActive()).count();
            
            report.put("companyMatrix", companyMatrix);
            report.put("jobMatrix", jobMatrix);
            report.put("systemMetrics", Map.of(
                "avgApplicationsPerJob", Math.round(avgApplicationsPerJob * 100.0) / 100.0,
                "avgJobsPerCompany", Math.round(avgJobsPerCompany * 100.0) / 100.0,
                "totalActiveJobs", activeJobs,
                "totalInactiveJobs", inactiveJobs
            ));
            
            report.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            report.put("reportType", "MATRIX");
            
        } catch (Exception e) {
            // Fallback data in case of error
            report.put("error", "Failed to generate matrix report: " + e.getMessage());
            report.put("companyMatrix", List.of());
            report.put("jobMatrix", List.of());
            report.put("systemMetrics", Map.of(
                "avgApplicationsPerJob", 0.0,
                "avgJobsPerCompany", 0.0,
                "totalActiveJobs", 0L,
                "totalInactiveJobs", 0L
            ));
        }
        
        return report;
    }
}