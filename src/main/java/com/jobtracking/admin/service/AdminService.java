package com.jobtracking.admin.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jobtracking.admin.dto.AdminCompanyResponse;
import com.jobtracking.admin.dto.AdminJobResponse;
import com.jobtracking.admin.dto.AdminStatsResponse;
import com.jobtracking.admin.dto.AdminUserResponse;
import com.jobtracking.application.repository.ApplicationRepository;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;

@Service
public class AdminService {
	private final UserRepository userRepo;
	private final JobRepository jobRepo;
	private final OrganizationRepository orgRepo;
	private final ApplicationRepository appRepo;

	public AdminService(UserRepository userRepo, JobRepository jobRepo, OrganizationRepository orgRepo,
			ApplicationRepository appRepo) {
		super();
		this.userRepo = userRepo;
		this.jobRepo = jobRepo;
		this.orgRepo = orgRepo;
		this.appRepo = appRepo;
	}

	public AdminStatsResponse getStats() {
		return new AdminStatsResponse(userRepo.count(), jobRepo.count(), orgRepo.count(), appRepo.count());
	}

	public List<AdminUserResponse> getAllUsers() {
		return userRepo.findAll().stream().map(u -> new AdminUserResponse(u.getId(), u.getUsername(), u.getEmail(),
				u.getRoleId().toString(), u.getCreatedAt())).toList();
	}

	public List<AdminJobResponse> getAllJobs() {
		Map<Long, String> companyNames = orgRepo.findAll().stream().collect(
				Collectors.toMap(Organization::getId, Organization::getName));

		return jobRepo.findAll().stream()
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
						c.getCreatedAt()
						))
				.toList();
	}

	public void updateUserStatus(Long userId, Boolean active) {
		userRepo.findById(userId).ifPresent(u -> {
			u.setActive(active);
			userRepo.save(u);
		});
	}

	public void updateUserRole(Long userId, Integer roleId) {
		userRepo.findById(userId).ifPresent(u -> {
			u.setRoleId(roleId);
			userRepo.save(u);
		});
	}

	public void deleteJob(Long jobId) {
		jobRepo.deleteById(jobId);
	}

	public void verifyJob(Long jobId) {
		jobRepo.findById(jobId).ifPresent(j -> {
			j.setIsActive(true);
			jobRepo.save(j);
		});
	}
}