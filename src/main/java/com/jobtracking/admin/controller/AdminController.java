package com.jobtracking.admin.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.admin.dto.AdminCompanyResponse;
import com.jobtracking.admin.dto.AdminJobResponse;
import com.jobtracking.admin.dto.AdminStatsResponse;
import com.jobtracking.admin.dto.AdminUserResponse;
import com.jobtracking.admin.service.AdminService;
import com.jobtracking.audit.entity.AuditLog;
import com.jobtracking.audit.service.AuditLogService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final AuditLogService auditLogService;
	private final AdminService adminService;

	public AdminController(AdminService adminService, AuditLogService auditLogService) {
		this.adminService = adminService;
		this.auditLogService = auditLogService;
	}

	@GetMapping("/stats")
	public AdminStatsResponse stats() {
		return adminService.getStats();
	}

	@GetMapping("/users")
	public List<AdminUserResponse> users() {
		return adminService.getAllUsers();
	}

	@GetMapping("/jobs")
	public List<AdminJobResponse> jobs() {
		return adminService.getAllJobs();
	}

	@GetMapping("/companies")
	public List<AdminCompanyResponse> companies() {
		return adminService.getAllCompanies();
	}

	@GetMapping("/logs")
	public List<AuditLog> logs() {
		return auditLogService.findAll();
	}

	@PatchMapping("/users/{id}/status")
	public void updateUserStatus(@PathVariable Long id, @RequestParam Boolean active) {
		adminService.updateUserStatus(id, active);
	}

	@PatchMapping("/users/{id}/role")
	public void updateUserRole(@PathVariable Long id, @RequestParam Integer roleId) {
		adminService.updateUserRole(id, roleId);
	}

	@DeleteMapping("/jobs/{id}")
	public void deleteJob(@PathVariable Long id) {
		adminService.deleteJob(id);
	}

	@PatchMapping("/jobs/{id}/verify")
	public void verifyJob(@PathVariable Long id) {
		adminService.verifyJob(id);
	}

}