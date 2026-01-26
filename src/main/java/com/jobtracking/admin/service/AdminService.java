package com.jobtracking.admin.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jobtracking.common.exception.ResourceNotFoundException;
import com.jobtracking.admin.dto.AdminApplicationResponse;
import com.jobtracking.admin.dto.AdminCompanyResponse;
import com.jobtracking.admin.dto.AdminJobResponse;
import com.jobtracking.admin.dto.AdminStatsResponse;
import com.jobtracking.admin.dto.AdminUserResponse;
import com.jobtracking.application.entity.Application;
import com.jobtracking.application.enums.ApplicationStatus;
import com.jobtracking.application.repository.ApplicationRepository;
import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;

@Service
public class AdminService {
	private final UserRepository userRepo;
	private final JobRepository jobRepo;
	private final OrganizationRepository orgRepo;
	private final ApplicationRepository appRepo;
	private final AuditLogService auditLogService;

	public AdminService(UserRepository userRepo, JobRepository jobRepo, OrganizationRepository orgRepo,
			ApplicationRepository appRepo, AuditLogService auditLogService) {
		super();
		this.userRepo = userRepo;
		this.jobRepo = jobRepo;
		this.orgRepo = orgRepo;
		this.appRepo = appRepo;
		this.auditLogService = auditLogService;
	}

	public AdminStatsResponse getStats() {
		long userCount = userRepo.count();
		long jobCount = jobRepo.count();
		long orgCount = orgRepo.count();
		long appCount = appRepo.count();

		return new AdminStatsResponse(userCount, jobCount, orgCount, appCount);
	}

	public List<AdminUserResponse> getAllUsers() {
		return userRepo.findAll().stream().map(u -> new AdminUserResponse(u.getId(), u.getUsername(), u.getEmail(),
				u.getRoleId().toString(), u.getActive(), u.getCreatedAt())).toList();
	}

	public List<AdminJobResponse> getAllJobs() {
		Map<Long, String> companyNames = getCompanyNamesMap();

		List<Job> jobs = jobRepo.findAll();

		return jobs.stream()
				.filter(j -> j.getDeletedAt() == null)
				.map(j -> new AdminJobResponse(j.getId(), j.getTitle(),
						companyNames.getOrDefault(j.getCompanyId(), "Unknown"), j.getIsActive(), j.getCreatedAt()))
				.toList();
	}

	private Map<Long, String> getCompanyNamesMap() {
		try {
			return orgRepo.findAll().stream().collect(
					Collectors.toMap(Organization::getId, Organization::getName));
		} catch (Exception e) {
			// Return empty map if companies can't be loaded
			return new HashMap<>();
		}
	}

	public List<AdminCompanyResponse> getAllCompanies() {
		return orgRepo.findAll().stream().map(
				c -> new AdminCompanyResponse(
						c.getId(),
						c.getName(),
						c.getWebsite(),
						c.getCity(),
						c.getContactEmail(),
						c.getVerified(),
						c.getCreatedAt()))
				.toList();
	}

	public void updateUserStatus(Long userId, Boolean active, Long adminId) {
		userRepo.findById(userId).ifPresent(u -> {
			u.setActive(active);
			userRepo.save(u);
			auditLogService.log("USER", userId, "Status changed to " + (active ? "ACTIVE" : "DISABLED"), adminId);
		});
	}

	public void updateUserRole(Long userId, Integer roleId, Long adminId) {
		userRepo.findById(userId).ifPresent(u -> {
			u.setRoleId(roleId);
			userRepo.save(u);
			auditLogService.log("USER", userId, "Role changed to " + roleId, adminId);
		});
	}

	public void deleteJob(Long jobId, Long adminId) {
		jobRepo.findById(jobId).ifPresent(j -> {
			j.setDeletedAt(LocalDateTime.now());
			jobRepo.save(j);
			auditLogService.log("JOB", jobId, "Deleted", adminId);
		});
	}

	public void verifyJob(Long jobId, Long adminId) {
		jobRepo.findById(jobId).ifPresentOrElse(j -> {
			// Toggle the active status
			j.setIsActive(!j.getIsActive());
			jobRepo.save(j);
			auditLogService.log("JOB", jobId,
					j.getIsActive() ? "Activated" : "Deactivated", adminId);
		}, () -> {
			throw new ResourceNotFoundException("Job not found with ID: " + jobId);
		});
	}

	public void verifyCompany(Long companyId, Boolean verified, Long adminId) {
		orgRepo.findById(companyId).ifPresent(c -> {
			c.setVerified(verified);
			orgRepo.save(c);
			auditLogService.log("COMPANY", companyId,
					"Verification changed to " + (verified ? "VERIFIED" : "UNVERIFIED"), adminId);
		});
	}

	public Page<AdminApplicationResponse> getAllApplications(int page, int size, String status) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());

		// Convert string status to enum if provided
		ApplicationStatus statusEnum = null;
		if (status != null && !status.isEmpty()) {
			try {
				statusEnum = ApplicationStatus.valueOf(status.toUpperCase());
			} catch (IllegalArgumentException e) {
				// Invalid status, will return empty results
			}
		}

		Page<Application> applications = appRepo.filterApplications(statusEnum, pageable);

		final Map<Long, String> jobTitles = jobRepo.findAll().stream().collect(
				Collectors.toMap(Job::getId, Job::getTitle));
		final Map<Long, String> userNames = userRepo.findAll().stream().collect(
				Collectors.toMap(User::getId, u -> u.getUsername()));

		return applications.map(a -> new AdminApplicationResponse(
				a.getId(),
				a.getJob().getId(),
				jobTitles.getOrDefault(a.getJob().getId(), "Unknown Job"),
				a.getUser().getId(),
				userNames.getOrDefault(a.getUser().getId(), "Unknown User"),
				a.getStatus().name(),
				a.getAppliedAt(),
				a.getUpdatedAt(),
				a.getResumePath()));
	}

	public AdminApplicationResponse getApplication(Long id) {
		Application application = appRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Application not found"));

		final Map<Long, String> jobTitles = jobRepo.findAll().stream().collect(
				Collectors.toMap(Job::getId, Job::getTitle));
		final Map<Long, String> userNames = userRepo.findAll().stream().collect(
				Collectors.toMap(User::getId, u -> u.getUsername()));

		return new AdminApplicationResponse(
				application.getId(),
				application.getJob().getId(),
				jobTitles.getOrDefault(application.getJob().getId(), "Unknown Job"),
				application.getUser().getId(),
				userNames.getOrDefault(application.getUser().getId(), "Unknown User"),
				application.getStatus().name(),
				application.getAppliedAt(),
				application.getUpdatedAt(),
				application.getResumePath());
	}

	public void deleteApplication(Long applicationId, Long adminId) {
		Application application = appRepo.findById(applicationId)
				.orElseThrow(() -> new ResourceNotFoundException("Application not found"));

		appRepo.delete(application);
		auditLogService.log("APPLICATION", applicationId, "ADMIN_DELETE", adminId);
	}
}
