package com.jobtracking.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.auth.dto.LoginRequest;
import com.jobtracking.auth.dto.LoginResponse;
import com.jobtracking.auth.dto.RegisterRequest;
import com.jobtracking.auth.entity.User;
import com.jobtracking.config.JwtUtil;
import com.jobtracking.auth.repository.UserRepository;

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
			throw new RuntimeException("Email already exists");
		}

		// Auto-generate username from email
		String autoUsername = generateUsernameFromEmail(request.getEmail());

		User user = User.builder()
				.username(autoUsername)
				.email(request.getEmail())
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.roleId(request.getRoleId())
				.fullname(request.getFullname())
				.active(true)
				.build();

		userRepository.save(user);
		
		// Log user registration
		auditLogService.log("USER", user.getId(), "REGISTERED", user.getId());
	}

	private String generateUsernameFromEmail(String email) {
		// Extract username part from email (before @)
		String baseUsername = email.substring(0, email.indexOf('@'));
		
		// Clean up the username (remove dots, special chars)
		baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		
		// Ensure uniqueness by checking database
		String username = baseUsername;
		int counter = 1;
		while (userRepository.existsByUsername(username)) {
			username = baseUsername + counter;
			counter++;
		}
		
		return username;
	}

	public LoginResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new RuntimeException("Invalid credentials");
		}

		String token = jwtUtil.generateToken(user.getId(), user.getRoleId());

		// Log successful login
		auditLogService.log("USER", user.getId(), "LOGIN", user.getId());

		return new LoginResponse(token, user.getId(), user.getRoleId(), user.getFullname(), user.getEmail());
	}
}
