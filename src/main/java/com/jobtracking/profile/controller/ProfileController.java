package com.jobtracking.profile.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.service.*;
import com.jobtracking.auth.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
     	private final ProfileService profileService;   	
//     	@GetMapping("/me")
//     	 public ProfileResponse getProfile(Authentication authentication) {
//
//            User user = (User) authentication.getPrincipal();
//            return profileService.getJobSeekerProfile(user.getId());
//        }
        @GetMapping("/jobseeker")
        public ProfileResponse getProfile(Authentication authentication) {
            Long userId = Long.valueOf(authentication.getPrincipal().toString());
            return profileService.getJobSeekerProfile(userId);
        }
}
