package com.jobtracking.profile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.RecruiterProfileResponseDTO;
import com.jobtracking.profile.dto.RecruiterUpdateProfile;
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

	@GetMapping("/recruiter")
	public ResponseEntity<?> getRecruiterProfile(Authentication authentication) {

		try {
			Long userId = Long.valueOf(authentication.getName());
			RecruiterProfileResponseDTO profile = profileService.getRecruiterProfile(userId);
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

	// 🔹 UPDATE recruiter profile
	@PutMapping("/recruiter")
	public ResponseEntity<?> updateRecruiterProfile(
			Authentication authentication,
			@RequestBody RecruiterUpdateProfile request) {
		try {
			Long userId = Long.valueOf(authentication.getName());
			profileService.updateRecruiterProfile(userId, request);
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
}
