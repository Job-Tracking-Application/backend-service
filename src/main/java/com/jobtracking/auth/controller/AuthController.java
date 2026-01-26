package com.jobtracking.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.auth.dto.LoginRequest;
import com.jobtracking.auth.dto.LoginResponse;
import com.jobtracking.auth.dto.RegisterRequest;
import com.jobtracking.auth.dto.SecureUserResponse;
import com.jobtracking.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
		authService.register(request);
		return ResponseEntity.ok("User registered successfully");
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok()
				.header("X-Rate-Limit", "10")
				.header("X-Rate-Limit-Window", "60")
				.body(response);
	}
	
	@GetMapping("/me")
	public ResponseEntity<SecureUserResponse> getCurrentUser(Authentication authentication) {
		SecureUserResponse userResponse = authService.getCurrentUser(authentication);
		return ResponseEntity.ok(userResponse);
	}
}
