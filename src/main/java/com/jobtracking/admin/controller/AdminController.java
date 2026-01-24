package com.jobtracking.admin.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.admin.dto.AdminApplicationResponse;
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

	private Long getCurrentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth == null || !auth.isAuthenticated()) {
			return null;
		}

		// The principal is the userId (String) set in JwtAuthenticationFilter
		Object principal = auth.getPrincipal();
		if (principal instanceof Long) {
			return (Long) principal;
		} else if (principal instanceof String) {
			try {
				return Long.parseLong((String) principal);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		return null;
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
		adminService.updateUserStatus(id, active, getCurrentUserId());
	}

	@PatchMapping("/users/{id}/role")
	public void updateUserRole(@PathVariable Long id, @RequestParam Integer roleId) {
		adminService.updateUserRole(id, roleId, getCurrentUserId());
	}

	@DeleteMapping("/jobs/{id}")
	public void deleteJob(@PathVariable Long id) {
		adminService.deleteJob(id, getCurrentUserId());
	}

	@PatchMapping("/jobs/{id}/verify")
	public ResponseEntity<String> verifyJob(@PathVariable Long id) {
		adminService.verifyJob(id, getCurrentUserId());
		return ResponseEntity.ok("Job verification completed");
	}

	@PatchMapping("/companies/{id}/verify")
	public void verifyCompany(@PathVariable Long id, @RequestParam Boolean verified) {
		adminService.verifyCompany(id, verified, getCurrentUserId());
	}

	// List all applications with pagination and filtering
	@GetMapping("/applications")
	public ResponseEntity<Page<AdminApplicationResponse>> getApplications(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String status) {
		
		return ResponseEntity.ok(
				adminService.getAllApplications(page, size, status));
	}

	// View single application
	@GetMapping("/applications/{id}")
	public ResponseEntity<AdminApplicationResponse> getApplication(@PathVariable Long id) {
		return ResponseEntity.ok(adminService.getApplication(id));
	}

	// Delete abusive application
	@DeleteMapping("/applications/{id}")
	public ResponseEntity<String> deleteApplication(@PathVariable Long id) {
		adminService.deleteApplication(id, getCurrentUserId());
		return ResponseEntity.ok("Application deleted successfully");
	}

}