package com.jobtracking.profile.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.entity.JobSeekerProfile;
import com.jobtracking.profile.repository.JobSeekerProfileRepository;
import com.jobtracking.profile.repository.JobSeekerSkillsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	private final JobSeekerProfileRepository jobSeekerProfileRepo;
	private final UserRepository userRepo;
	private final JobSeekerSkillsRepository jobSeekerSkills;
	public ProfileResponse getJobSeekerProfile(Long id){
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(id)
                .orElse(null);
        ProfileResponse dto = new ProfileResponse();
        dto.setFullName(user.getFullname());
        dto.setEmail(user.getEmail());
        dto.setUserName(user.getUsername());
        
        if (profile != null) {
        	List<String> skills=jobSeekerSkills.findByJobSeekerProfile(profile).stream().map(jsSkill->jsSkill.getSkill().getName()).toList();
            dto.setSkills(skills);
        	dto.setResume(profile.getResumeLink());
            dto.setAbout(profile.getBioEn());
            dto.setEducation(profile.getEducation());
        }
        return dto;
	}

}
