package com.jobtracking.application.service;

import java.util.List;
import com.jobtracking.application.entity.Application;
import com.jobtracking.application.repository.ApplicationRepository;
import com.jobtracking.profile.repository.JobSeekerProfileRepository;
import com.jobtracking.profile.entity.JobSeekerProfile;
import com.jobtracking.auth.entity.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.jobtracking.application.dto.CandidateApplicationResponse;
import com.jobtracking.application.dto.ApplicationResponse;
import com.jobtracking.application.dto.ApplyJobRequest;
import com.jobtracking.application.enums.ApplicationStatus;
import com.jobtracking.application.dto.UpdateStatusRequest;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.repository.OrganizationRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public void createApplication(Long jobId, Long userId, ApplyJobRequest applyJobRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        // 🔒 First guard: service-level duplicate check
        if (applicationRepository.existsByJob_IdAndUser_Id(jobId, userId)) {
            throw new IllegalStateException("You have already applied for this job");
        }

        
        Application application = new Application();
        application.setJob(job);
        application.setUser(user);
        application.setResumePath(applyJobRequest.resume());
        try {
            applicationRepository.save(application);
        } catch (DataIntegrityViolationException ex) {
            // 🔒 Second guard: DB-level safety (race condition)
            throw new IllegalStateException("You have already applied for this job");
        }
    }

    @Override
    public List<CandidateApplicationResponse> getCandidateApplication(Long userId) {
        return applicationRepository.findByUserId(userId)
                .stream()
                .map(application -> {
                    // Get company name by companyId
                    String companyName = "Unknown Company";
                    if (application.getJob().getCompanyId() != null) {
                        companyName = organizationRepository.findById(application.getJob().getCompanyId())
                                .map(org -> org.getName())
                                .orElse("Unknown Company");
                    }
                    
                    return new CandidateApplicationResponse(
                            application.getId(),
                            application.getJob().getTitle(),
                            companyName,
                            application.getStatus().name(),
                            application.getAppliedAt().toLocalDate(),
                            application.getResumePath());
                })
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplication(Long jobId) {
        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();
    }

    @Override
    public ApplicationResponse updateApplication(Long id, UpdateStatusRequest updateStatusRequest) {
        return applicationRepository.findById(id)
                .map(application -> {
                    application.setStatus(ApplicationStatus.valueOf(updateStatusRequest.status().toUpperCase()));
                    return applicationRepository.save(application);
                })
                .map(this::mapToApplicationResponse)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    @Override
    public boolean hasUserAppliedForJob(Long jobId, Long userId) {
        return applicationRepository.existsByJobIdAndUserId(jobId, userId);
    }

    private ApplicationResponse mapToApplicationResponse(Application application) {
        try {
            User user = application.getUser();
            JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(user.getId()).orElse(null);
            List<String> skills = List.of(); // Default empty list
            
            if (profile != null && profile.getSkills() != null) {
                try {
                    skills = profile.getSkills().stream()
                            .map(s -> s.getSkill().getName())
                            .toList();
                } catch (Exception e) {
                    System.err.println("Error loading skills for user " + user.getId() + ": " + e.getMessage());
                    // Keep skills as empty list
                }
            }

            return new ApplicationResponse(
                    application.getId(),
                    user.getFullname(),
                    user.getEmail(),
                    skills,
                    application.getStatus().name(),
                    application.getResumePath());
        } catch (Exception e) {
            System.err.println("Error mapping application response: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error processing application data", e);
        }
    }

}
