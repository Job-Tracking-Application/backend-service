package com.jobtracking.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtracking.admin.dto.AdminUserResponse;
import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.common.utils.ValidationUtil;

import lombok.RequiredArgsConstructor;

/**
 * Service for admin user management operations
 * Follows Single Responsibility Principle - only handles user-related admin operations
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    /**
     * Get all users for admin view
     */
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoleId().toString(),
                        user.getActive(),
                        user.getCreatedAt()))
                .toList();
    }

    /**
     * Update user active status
     */
    public void updateUserStatus(Long userId, Boolean active, Long adminId) {
        ValidationUtil.validateNotNull(userId, "User ID cannot be null");
        ValidationUtil.validateNotNull(active, "Active status cannot be null");
        ValidationUtil.validateNotNull(adminId, "Admin ID cannot be null");

        userRepository.findById(userId).ifPresentOrElse(user -> {
            user.setActive(active);
            userRepository.save(user);
            
            String action = active ? "ACTIVATED" : "DEACTIVATED";
            auditLogService.log("USER", userId, action, adminId, 
                "User status changed to " + (active ? "ACTIVE" : "DISABLED"));
        }, () -> {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        });
    }

    /**
     * Update user role
     */
    public void updateUserRole(Long userId, Integer roleId, Long adminId) {
        ValidationUtil.validateNotNull(userId, "User ID cannot be null");
        ValidationUtil.validateNotNull(roleId, "Role ID cannot be null");
        ValidationUtil.validateNotNull(adminId, "Admin ID cannot be null");
        ValidationUtil.validateRange(roleId, 1, 3, "Role ID must be between 1 and 3");

        userRepository.findById(userId).ifPresentOrElse(user -> {
            Integer oldRoleId = user.getRoleId();
            user.setRoleId(roleId);
            userRepository.save(user);
            
            auditLogService.log("USER", userId, "ROLE_CHANGED", adminId,
                "Role changed from " + oldRoleId + " to " + roleId);
        }, () -> {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        });
    }

    /**
     * Get user count for statistics
     */
    public long getUserCount() {
        return userRepository.count();
    }
}