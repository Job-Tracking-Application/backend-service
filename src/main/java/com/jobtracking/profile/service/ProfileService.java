
package com.jobtracking.profile.service;

import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.RecruiterProfileResponse;
import com.jobtracking.profile.dto.UpdateProfileRequest;
import com.jobtracking.profile.dto.UpdateRecruiterProfileRequest;

public interface ProfileService{
	
	ProfileResponse getJobSeekerProfile(Long id);

	void updateJobSeekerProfile(Long userId, UpdateProfileRequest request);
	
	RecruiterProfileResponse getRecruiterProfile(Long userId);
	
	void updateRecruiterProfile(Long userId, UpdateRecruiterProfileRequest request);
}