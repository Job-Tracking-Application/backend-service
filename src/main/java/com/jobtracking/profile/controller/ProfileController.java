package com.jobtracking.profile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.UpdateProfileRequest;
import com.jobtracking.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
	private final ProfileService profileService;

	@GetMapping("/jobseeker")
	public ResponseEntity<?> getProfile(Authentication authentication) {

		try {
			Long userId = Long.valueOf(authentication.getName());
			ProfileResponse profile = profileService.getJobSeekerProfile(userId);
			return ResponseEntity.ok(profile);

		} catch (NumberFormatException ex) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Invalid user id");

		} catch (RuntimeException ex) {
			// user not found / profile not found
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(ex.getMessage());

		}
	}

	// 🔹 UPDATE jobseeker profile
	@PutMapping("/jobseeker")
	public ResponseEntity<?> updateProfile(
			Authentication authentication,
			@RequestBody UpdateProfileRequest request) {

		try {
			Long userId = Long.valueOf(authentication.getName());
			profileService.updateJobSeekerProfile(userId, request);
			return ResponseEntity.ok("Profile updated successfully");

		} catch (NumberFormatException ex) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Invalid user id");

		} catch (RuntimeException ex) {
			// user not found or profile error
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(ex.getMessage());

		}
	}

	// 🔹 TEST ENDPOINT - Create demo skills for current user
	@PostMapping("/jobseeker/create-demo-skills")
	public ResponseEntity<?> createDemoSkills(Authentication authentication) {
		try {
			Long userId = Long.valueOf(authentication.getName());
			profileService.createDemoSkillsForUser(userId);
			return ResponseEntity.ok("Demo skills created successfully");
		} catch (Exception ex) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating demo skills: " + ex.getMessage());
		}
	}

	// 🔹 DEBUG ENDPOINT - Get raw profile data
	@GetMapping("/jobseeker/debug")
	public ResponseEntity<?> debugProfile(Authentication authentication) {
		try {
			Long userId = Long.valueOf(authentication.getName());
			ProfileResponse profile = profileService.getJobSeekerProfile(userId);
			
			// Return detailed debug info
			java.util.Map<String, Object> debugInfo = new java.util.HashMap<>();
			debugInfo.put("userId", userId);
			debugInfo.put("profile", profile);
			debugInfo.put("educationValue", profile.getEducation());
			debugInfo.put("educationIsNull", profile.getEducation() == null);
			debugInfo.put("educationIsEmpty", profile.getEducation() != null && profile.getEducation().isEmpty());
			
			return ResponseEntity.ok(debugInfo);
		} catch (Exception ex) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Debug error: " + ex.getMessage());
		}
	}
}
