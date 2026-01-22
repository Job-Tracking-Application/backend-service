package com.jobtracking.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jobtracking.admin.dto.AdminApplicationResponse;
import com.jobtracking.admin.dto.AdminCompanyResponse;
import com.jobtracking.admin.dto.AdminJobResponse;
import com.jobtracking.admin.dto.AdminStatsResponse;
import com.jobtracking.admin.dto.AdminUserResponse;
import com.jobtracking.application.entity.Application;
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
		return new AdminStatsResponse(userRepo.count(), jobRepo.count(), orgRepo.count(), appRepo.count());
	}

	public List<AdminUserResponse> getAllUsers() {
		return userRepo.findAll().stream().map(u -> new AdminUserResponse(u.getId(), u.getUsername(), u.getEmail(),
				u.getRoleId().toString(), u.getActive(), u.getCreatedAt())).toList();
	}

	public List<AdminJobResponse> getAllJobs() {
		Map<Long, String> companyNames = orgRepo.findAll().stream().collect(
				Collectors.toMap(Organization::getId, Organization::getName));

		return jobRepo.findAll().stream()
				.filter(j -> j.getDeletedAt() == null)
				.map(j -> new AdminJobResponse(j.getId(), j.getTitle(),
						companyNames.getOrDefault(j.getCompanyId(), "Unknown"), j.getIsActive(), j.getCreatedAt()))
				.toList();
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
		jobRepo.findById(jobId).ifPresent(j -> {
			j.setIsActive(true);
			jobRepo.save(j);
			auditLogService.log("JOB", jobId, "Verified", adminId);
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
		
		Page<Application> applications = appRepo.filterApplications(status, pageable);
		
		Map<Long, String> jobTitles = jobRepo.findAll().stream().collect(
				Collectors.toMap(Job::getId, Job::getTitle));
		Map<Long, String> userNames = userRepo.findAll().stream().collect(
				Collectors.toMap(User::getId, u -> u.getUsername()));

		return applications.map(a -> new AdminApplicationResponse(
				a.getId(),
				a.getJobId(),
				jobTitles.getOrDefault(a.getJobId(), "Unknown Job"),
				a.getJobSeekerUserId(),
				userNames.getOrDefault(a.getJobSeekerUserId(), "Unknown User"),
				a.getStatus(),
				a.getAppliedAt(),
				a.getUpdatedAt(),
				a.getResumePath()));
	}

	public AdminApplicationResponse getApplication(Long id) {
		Application application = appRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Application not found"));
		
		Map<Long, String> jobTitles = jobRepo.findAll().stream().collect(
				Collectors.toMap(Job::getId, Job::getTitle));
		Map<Long, String> userNames = userRepo.findAll().stream().collect(
				Collectors.toMap(User::getId, u -> u.getUsername()));

		return new AdminApplicationResponse(
				application.getId(),
				application.getJobId(),
				jobTitles.getOrDefault(application.getJobId(), "Unknown Job"),
				application.getJobSeekerUserId(),
				userNames.getOrDefault(application.getJobSeekerUserId(), "Unknown User"),
				application.getStatus(),
				application.getAppliedAt(),
				application.getUpdatedAt(),
				application.getResumePath());
	}

	public void deleteApplication(Long applicationId, Long adminId) {
		Application application = appRepo.findById(applicationId)
				.orElseThrow(() -> new RuntimeException("Application not found"));
		
		appRepo.delete(application);
		auditLogService.log("APPLICATION", applicationId, "ADMIN_DELETE", adminId);
	}
}
