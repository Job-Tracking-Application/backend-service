package com.jobtracking.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.RecruiterProfileResponse;
import com.jobtracking.profile.dto.UpdateProfileRequest;
import com.jobtracking.profile.dto.UpdateRecruiterProfileRequest;
import com.jobtracking.profile.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
	private final ProfileService profileService;

	@GetMapping("/jobseeker")
	public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
		Long userId = Long.valueOf(authentication.getName());
		ProfileResponse profile = profileService.getJobSeekerProfile(userId);
		return ResponseEntity.ok(profile);
	}

	// 🔹 UPDATE jobseeker profile
	@PutMapping("/jobseeker")
	public ResponseEntity<String> updateProfile(
			Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest request) {
		Long userId = Long.valueOf(authentication.getName());
		profileService.updateJobSeekerProfile(userId, request);
		return ResponseEntity.ok("Profile updated successfully");
	}

	@GetMapping("/recruiter")
	public ResponseEntity<RecruiterProfileResponse> getRecruiterProfile(Authentication authentication) {
		Long userId = Long.valueOf(authentication.getName());
		RecruiterProfileResponse profile = profileService.getRecruiterProfile(userId);
		return ResponseEntity.ok(profile);
	}

	// 🔹 UPDATE recruiter profile
	@PutMapping("/recruiter")
	public ResponseEntity<RecruiterProfileResponse> updateRecruiterProfile(
			Authentication authentication,
			@RequestBody UpdateRecruiterProfileRequest request) {
		Long userId = Long.valueOf(authentication.getName());
		profileService.updateRecruiterProfile(userId, request);

		// Return the updated profile data instead of just a success message
		RecruiterProfileResponse updatedProfile = profileService.getRecruiterProfile(userId);
		return ResponseEntity.ok(updatedProfile);
	}

}