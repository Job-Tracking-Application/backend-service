
package com.jobtracking.profile.service;

import com.jobtracking.profile.dto.ProfileResponse;

public interface ProfileService{
	
	ProfileResponse getJobSeekerProfile(Long id);
}