package com.jobtracking.application.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.common.exception.ConflictException;
import com.jobtracking.common.exception.ResourceNotFoundException;
import com.jobtracking.application.dto.ApplicationResponse;
import com.jobtracking.application.dto.ApplyJobRequest;
import com.jobtracking.application.dto.CandidateApplicationResponse;
import com.jobtracking.application.dto.UpdateStatusRequest;
import com.jobtracking.application.entity.Application;
import com.jobtracking.application.enums.ApplicationStatus;
import com.jobtracking.application.repository.ApplicationRepository;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.profile.entity.JobSeekerProfile;
import com.jobtracking.profile.repository.JobSeekerProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void createApplication(Long jobId, Long userId, ApplyJobRequest applyJobRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        // First guard: service-level duplicate check
        if (applicationRepository.existsByJobIdAndUserId(jobId, userId)) {
            throw new ConflictException("You have already applied for this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setUser(user);
        application.setResumePath(applyJobRequest.resume());
        application.setCoverLetter(applyJobRequest.coverLetter());

        // Store additional fields in extension JSON
        if (hasAdditionalFields(applyJobRequest)) {
            String extensionJson = buildExtensionJson(applyJobRequest);
            application.setExtension(extensionJson);
        }

        try {
            Application savedApplication = applicationRepository.save(application);

            // Log job application
            auditLogService.log("APPLICATION", savedApplication.getId(), "APPLIED", userId,
                    "Applied for job: " + job.getTitle());

        } catch (DataIntegrityViolationException ex) {
            // Second guard: DB-level safety (race condition)
            throw new ConflictException("You have already applied for this job");
        }
    }

    private boolean hasAdditionalFields(ApplyJobRequest request) {
        return (request.portfolioUrl() != null && !request.portfolioUrl().trim().isEmpty()) ||
                (request.linkedinUrl() != null && !request.linkedinUrl().trim().isEmpty()) ||
                (request.githubUrl() != null && !request.githubUrl().trim().isEmpty()) ||
                (request.additionalNotes() != null && !request.additionalNotes().trim().isEmpty());
    }

    private String buildExtensionJson(ApplyJobRequest request) {
        StringBuilder json = new StringBuilder("{");
        boolean hasField = false;

        if (request.portfolioUrl() != null && !request.portfolioUrl().trim().isEmpty()) {
            json.append("\"portfolioUrl\":\"").append(request.portfolioUrl().trim()).append("\"");
            hasField = true;
        }

        if (request.linkedinUrl() != null && !request.linkedinUrl().trim().isEmpty()) {
            if (hasField)
                json.append(",");
            json.append("\"linkedinUrl\":\"").append(request.linkedinUrl().trim()).append("\"");
            hasField = true;
        }

        if (request.githubUrl() != null && !request.githubUrl().trim().isEmpty()) {
            if (hasField)
                json.append(",");
            json.append("\"githubUrl\":\"").append(request.githubUrl().trim()).append("\"");
            hasField = true;
        }

        if (request.additionalNotes() != null && !request.additionalNotes().trim().isEmpty()) {
            if (hasField)
                json.append(",");
            json.append("\"additionalNotes\":\"").append(request.additionalNotes().trim().replace("\"", "\\\""))
                    .append("\"");
        }

        json.append("}");
        return json.toString();
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
                    ApplicationStatus oldStatus = application.getStatus();
                    ApplicationStatus newStatus = ApplicationStatus.valueOf(updateStatusRequest.status().toUpperCase());

                    application.setStatus(newStatus);
                    Application savedApplication = applicationRepository.save(application);

                    // Log status change (performed by recruiter/admin)
                    auditLogService.log("APPLICATION", savedApplication.getId(), "STATUS_CHANGED",
                            savedApplication.getJob().getRecruiterUserId(),
                            "Changed from " + oldStatus + " to " + newStatus);

                    return savedApplication;
                })
                .map(this::mapToApplicationResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
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
                    // Keep skills as empty list
                }
            }

            // Parse extension JSON to extract additional fields
            String portfolioUrl = null;
            String linkedinUrl = null;
            String githubUrl = null;
            String additionalNotes = null;

            if (application.getExtension() != null && !application.getExtension().trim().isEmpty()) {
                try {
                    String extension = application.getExtension();

                    // Use Jackson ObjectMapper for proper JSON parsing
                    JsonNode jsonNode = objectMapper.readTree(extension);

                    portfolioUrl = jsonNode.has("portfolioUrl") ? jsonNode.get("portfolioUrl").asText(null) : null;
                    linkedinUrl = jsonNode.has("linkedinUrl") ? jsonNode.get("linkedinUrl").asText(null) : null;
                    githubUrl = jsonNode.has("githubUrl") ? jsonNode.get("githubUrl").asText(null) : null;
                    additionalNotes = jsonNode.has("additionalNotes") ? jsonNode.get("additionalNotes").asText(null)
                            : null;

                    // Clean up empty strings
                    portfolioUrl = (portfolioUrl != null && portfolioUrl.trim().isEmpty()) ? null : portfolioUrl;
                    linkedinUrl = (linkedinUrl != null && linkedinUrl.trim().isEmpty()) ? null : linkedinUrl;
                    githubUrl = (githubUrl != null && githubUrl.trim().isEmpty()) ? null : githubUrl;
                    additionalNotes = (additionalNotes != null && additionalNotes.trim().isEmpty()) ? null
                            : additionalNotes;

                } catch (Exception e) {
                    // Keep additional fields as null if parsing fails
                }
            }

            return new ApplicationResponse(
                    application.getId(),
                    user.getFullname(),
                    user.getEmail(),
                    skills,
                    application.getStatus().name(),
                    application.getResumePath(),
                    application.getCoverLetter(),
                    portfolioUrl,
                    linkedinUrl,
                    githubUrl,
                    additionalNotes);
        } catch (Exception e) {
            throw new RuntimeException("Error processing application data", e);
        }
    }
}
