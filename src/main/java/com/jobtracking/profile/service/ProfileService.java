
package com.jobtracking.profile.service;

import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.UpdateProfileRequest;

public interface ProfileService{
	
	ProfileResponse getJobSeekerProfile(Long id);

	void updateJobSeekerProfile(Long userId, UpdateProfileRequest request);
	
	void createDemoSkillsForUser(Long userId);
}