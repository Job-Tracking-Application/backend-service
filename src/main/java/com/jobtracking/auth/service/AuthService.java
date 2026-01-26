package com.jobtracking.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.auth.dto.LoginRequest;
import com.jobtracking.auth.dto.LoginResponse;
import com.jobtracking.auth.dto.RegisterRequest;
import com.jobtracking.auth.dto.SecureUserResponse;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.common.exception.ConflictException;
import com.jobtracking.common.exception.ResourceNotFoundException;
import com.jobtracking.common.exception.UnauthorizedException;
import com.jobtracking.common.utils.DataMaskingUtil;
import com.jobtracking.config.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final AuditLogService auditLogService;

	public void register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new ConflictException("Email already exists");
		}

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new ConflictException("Username already exists");
		}

		User user = User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.roleId(request.getRoleId())
				.fullname(request.getFullname())
				.phone(request.getPhone())
				.active(true)
				.build();

		userRepository.save(user);

		// Log user registration
		auditLogService.log("USER", user.getId(), "REGISTERED", user.getId());
	}

	public LoginResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new UnauthorizedException("Invalid credentials");
		}

		if (user.getActive() != null && !user.getActive()) {
			throw new UnauthorizedException("User account is deactivated");
		}

		String token = jwtUtil.generateToken(user.getId(), user.getRoleId());

		// Log successful login
		auditLogService.log("USER", user.getId(), "LOGIN", user.getId());

		return new LoginResponse(token, user.getId(), user.getRoleId(), user.getFullname(), user.getEmail());
	}

	public SecureUserResponse getCurrentUser(Authentication authentication) {
		// Extract user ID from JWT token (stored in authentication subject)
		String userIdStr = authentication.getName();
		Long userId = Long.parseLong(userIdStr);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Check if user is still active
		if (!user.getActive()) {
			throw new UnauthorizedException("User account is deactivated");
		}

		// Log the authentication check for security audit
		auditLogService.log("USER", userId, "AUTH_CHECK", userId);

		// Return minimal, masked user data for security
		return SecureUserResponse.builder()
				.roleId(user.getRoleId())
				.role(DataMaskingUtil.mapRoleIdToName(user.getRoleId()))
				.displayName(DataMaskingUtil.createDisplayName(user.getFullname()))
				.maskedEmail(DataMaskingUtil.maskEmail(user.getEmail()))
				.active(user.getActive())
				.languagePref(user.getLanguagePref() != null ? user.getLanguagePref() : "en")
				.accountType("STANDARD") // Could be enhanced based on user tier
				.build();
	}
}
