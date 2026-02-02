package com.jobtracking.admin.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.jobtracking.admin.service.AdminApplicationService;
import com.jobtracking.admin.service.AdminCompanyService;
import com.jobtracking.admin.service.AdminJobService;
import com.jobtracking.admin.service.AdminStatsService;
import com.jobtracking.admin.service.AdminUserService;
import com.jobtracking.audit.entity.AuditLog;
import com.jobtracking.audit.repository.AuditLogRepository;
import com.jobtracking.common.controller.BaseController;
import com.jobtracking.common.response.ApiResponse;
import com.jobtracking.common.utils.ResponseUtil;

/**
 * Admin controller using focused services following SRP
 * Each service handles a specific domain of admin operations
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController extends BaseController {

    private final AdminUserService adminUserService;
    private final AdminJobService adminJobService;
    private final AdminCompanyService adminCompanyService;
    private final AdminApplicationService adminApplicationService;
    private final AdminStatsService adminStatsService;
    private final AuditLogRepository auditLogRepository;

    public AdminController(
            AdminUserService adminUserService,
            AdminJobService adminJobService,
            AdminCompanyService adminCompanyService,
            AdminApplicationService adminApplicationService,
            AdminStatsService adminStatsService,
            AuditLogRepository auditLogRepository) {
        this.adminUserService = adminUserService;
        this.adminJobService = adminJobService;
        this.adminCompanyService = adminCompanyService;
        this.adminApplicationService = adminApplicationService;
        this.adminStatsService = adminStatsService;
        this.auditLogRepository = auditLogRepository;
    }

    // Statistics endpoints
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        try {
            AdminStatsResponse stats = adminStatsService.getStats();
            return ResponseUtil.success(stats, "Statistics retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("Failed to retrieve statistics: " + e.getMessage());
        }
    }

    // User management endpoints
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {
        try {
            List<AdminUserResponse> users = adminUserService.getAllUsers();
            return ResponseUtil.success(users, "Users retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("Failed to retrieve users: " + e.getMessage());
        }
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long userId, 
            @RequestParam Boolean active) {
        try {
            Long adminId = getCurrentUserId();
            adminUserService.updateUserStatus(userId, active, adminId);
            return ResponseUtil.success(null, "User status updated successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to update user status: " + e.getMessage());
        }
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long userId, 
            @RequestParam Integer roleId) {
        try {
            Long adminId = getCurrentUserId();
            adminUserService.updateUserRole(userId, roleId, adminId);
            return ResponseUtil.success(null, "User role updated successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to update user role: " + e.getMessage());
        }
    }

    // Job management endpoints
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<AdminJobResponse>>> getAllJobs() {
        try {
            List<AdminJobResponse> jobs = adminJobService.getAllJobs();
            return ResponseUtil.success(jobs, "Jobs retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("Failed to retrieve jobs: " + e.getMessage());
        }
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long jobId) {
        try {
            Long adminId = getCurrentUserId();
            adminJobService.deleteJob(jobId, adminId);
            return ResponseUtil.success(null, "Job deleted successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to delete job: " + e.getMessage());
        }
    }

    @PatchMapping("/jobs/{jobId}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleJobStatus(@PathVariable Long jobId) {
        try {
            Long adminId = getCurrentUserId();
            adminJobService.toggleJobStatus(jobId, adminId);
            return ResponseUtil.success(null, "Job status updated successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to update job status: " + e.getMessage());
        }
    }

    // Company management endpoints
    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<AdminCompanyResponse>>> getAllCompanies() {
        try {
            List<AdminCompanyResponse> companies = adminCompanyService.getAllCompanies();
            return ResponseUtil.success(companies, "Companies retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("Failed to retrieve companies: " + e.getMessage());
        }
    }

    @PatchMapping("/companies/{companyId}/verify")
    public ResponseEntity<ApiResponse<Void>> updateCompanyVerification(
            @PathVariable Long companyId, 
            @RequestParam Boolean verified) {
        try {
            Long adminId = getCurrentUserId();
            adminCompanyService.updateCompanyVerification(companyId, verified, adminId);
            return ResponseUtil.success(null, "Company verification updated successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to update company verification: " + e.getMessage());
        }
    }

    // Application management endpoints
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<Page<AdminApplicationResponse>>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            Page<AdminApplicationResponse> applications = 
                adminApplicationService.getAllApplications(page, size, status);
            return ResponseUtil.success(applications, "Applications retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to retrieve applications: " + e.getMessage());
        }
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<AdminApplicationResponse>> getApplication(@PathVariable Long applicationId) {
        try {
            AdminApplicationResponse application = adminApplicationService.getApplicationById(applicationId);
            return ResponseUtil.success(application, "Application retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.notFound("Application not found: " + e.getMessage());
        }
    }

    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long applicationId) {
        try {
            Long adminId = getCurrentUserId();
            adminApplicationService.deleteApplication(applicationId, adminId);
            return ResponseUtil.success(null, "Application deleted successfully");
        } catch (Exception e) {
            return ResponseUtil.error("Failed to delete application: " + e.getMessage());
        }
    }

    // Audit logs endpoints
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) String action) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditLog> logs;
            
            if (entity != null && !entity.isEmpty()) {
                logs = auditLogRepository.findByEntityWithPagination(entity, pageable);
            } else {
                logs = auditLogRepository.findAllWithPagination(pageable);
            }
            
            return ResponseUtil.success(logs, "Audit logs retrieved successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("Failed to retrieve audit logs: " + e.getMessage());
        }
    }
}