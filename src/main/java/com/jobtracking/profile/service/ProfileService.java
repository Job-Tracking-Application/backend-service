
package com.jobtracking.profile.service;

import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.RecruiterProfileResponseDTO;
import com.jobtracking.profile.dto.RecruiterUpdateProfile;
import com.jobtracking.profile.dto.UpdateProfileRequest;

public interface ProfileService {

	ProfileResponse getJobSeekerProfile(Long id);

	void updateJobSeekerProfile(Long userId, UpdateProfileRequest request);

	RecruiterProfileResponseDTO getRecruiterProfile(Long userId);

	void updateRecruiterProfile(Long userId, RecruiterUpdateProfile request);
}