package com.jobtracking.profile.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracking.audit.service.AuditLogService;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.profile.dto.EducationDTO;
import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.RecruiterProfileResponse;
import com.jobtracking.profile.dto.UpdateProfileRequest;
import com.jobtracking.profile.dto.UpdateRecruiterProfileRequest;
import com.jobtracking.profile.entity.JobSeekerProfile;
import com.jobtracking.profile.entity.JobSeekerSkill;
import com.jobtracking.profile.entity.RecruiterProfile;
import com.jobtracking.profile.entity.Skill;
import com.jobtracking.profile.enums.Proficiency;
import com.jobtracking.profile.repository.JobSeekerProfileRepository;
import com.jobtracking.profile.repository.JobSeekerSkillsRepository;
import com.jobtracking.profile.repository.RecruiterProfileRepository;
import com.jobtracking.profile.repository.SkillRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	private final JobSeekerProfileRepository jobSeekerProfileRepo;
	private final RecruiterProfileRepository recruiterProfileRepo;
	private final UserRepository userRepo;
	private final JobSeekerSkillsRepository jobSeekerSkills;
	private final SkillRepository skillRepo;
	private final AuditLogService auditLogService;

	public ProfileResponse getJobSeekerProfile(Long id) {
		User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		
		// Get or create profile if it doesn't exist
		JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(id)
				.orElseGet(() -> {
					// Create a new profile if it doesn't exist
					JobSeekerProfile newProfile = new JobSeekerProfile();
					newProfile.setUser(user);
					return jobSeekerProfileRepo.save(newProfile);
				});
		
		// Fetch skills with error handling
		List<String> skills;
		try {
			skills = jobSeekerSkills.findByJobSeekerProfile(profile).stream()
					.map(jsSkill -> jsSkill.getSkill().getName()).toList();
		} catch (Exception e) {
			skills = java.util.List.of(); // Set empty list on error
		}
		
		// Education (JSON string → Object)
		EducationDTO education = null;
		if (profile.getEducation() != null && !profile.getEducation().trim().isEmpty()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				education = mapper.readValue(profile.getEducation(), EducationDTO.class);
			} catch (Exception e) {
				education = null;
			}
		}
		
		// Return record using constructor
		return new ProfileResponse(
				user.getFullname(),
				user.getEmail(),
				user.getUsername(),
				skills,
				profile.getResumeLink(),
				profile.getBioEn(),
				education
		);
	}

	@Override
	public void updateJobSeekerProfile(Long userId, UpdateProfileRequest req) {
		// 1️⃣ Fetch user
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setFullname(req.fullName());
		// Username is auto-generated, not user-editable
		userRepo.save(user);

		// 2️⃣ Fetch or create profile
		JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(userId)
				.orElseGet(() -> {
					JobSeekerProfile p = new JobSeekerProfile();
					p.setUser(user);
					return p;
				});

		profile.setBioEn(req.about());
		profile.setResumeLink(req.resume());

		// 3️⃣ Handle education field
		if (req.education() != null && !req.education().trim().isEmpty()) {
			// If it looks like JSON, use it as is
			if (req.education().trim().startsWith("{")) {
				profile.setEducation(req.education());
			} else {
				// Convert plain text to JSON format
				try {
					ObjectMapper mapper = new ObjectMapper();
					EducationDTO educationDTO = new EducationDTO(req.education(), "", 0);
					profile.setEducation(mapper.writeValueAsString(educationDTO));
				} catch (Exception e) {
					// Fallback: store as plain text
					profile.setEducation(req.education());
				}
			}
		}

		// Save profile first to ensure it has an ID
		profile = jobSeekerProfileRepo.save(profile);

		// 4️⃣ Replace skills (delete + insert)
		jobSeekerSkills.deleteByJobSeekerProfileId(profile.getId());

		if (req.skills() != null) {
			for (String skillName : req.skills()) {
				if (skillName != null && !skillName.trim().isEmpty()) {
					Skill skill = skillRepo.findByName(skillName.trim())
							.orElseGet(() -> {
								Skill s = new Skill();
								s.setName(skillName.trim());
								return skillRepo.save(s);
							});

					JobSeekerSkill js = new JobSeekerSkill();
					js.setJobSeekerProfile(profile);
					js.setSkill(skill);
					js.setProficiency(Proficiency.INTERMEDIATE); // default
					jobSeekerSkills.save(js);
				}
			}
		}
		
		// Log profile update
		auditLogService.log("PROFILE", profile.getId(), "UPDATED", userId);
	}

	@Override
	public RecruiterProfileResponse getRecruiterProfile(Long userId) {
		User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		
		// Get or create profile if it doesn't exist
		RecruiterProfile profile = recruiterProfileRepo.findByUserId(userId)
				.orElseGet(() -> {
					// Create a new profile if it doesn't exist
					RecruiterProfile newProfile = new RecruiterProfile();
					newProfile.setUser(user);
					return recruiterProfileRepo.save(newProfile);
				});
		
		// Return record using constructor
		return new RecruiterProfileResponse(
				user.getFullname(),
				user.getEmail(),
				user.getUsername(),
				profile.getBioEn(),
				profile.getPhone(),
				profile.getLinkedinUrl(),
				profile.getYearsExperience(),
				profile.getSpecialization()
		);
	}

	@Override
	public void updateRecruiterProfile(Long userId, UpdateRecruiterProfileRequest req) {
		// 1️⃣ Fetch user
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setFullname(req.fullName());
		userRepo.save(user);

		// 2️⃣ Fetch or create profile
		RecruiterProfile profile = recruiterProfileRepo.findByUserId(userId)
				.orElseGet(() -> {
					RecruiterProfile p = new RecruiterProfile();
					p.setUser(user);
					return p;
				});

		profile.setBioEn(req.bio());
		profile.setPhone(req.phone());
		profile.setLinkedinUrl(req.linkedinUrl());
		profile.setYearsExperience(req.yearsExperience());
		profile.setSpecialization(req.specialization());

		// Save profile
		profile = recruiterProfileRepo.save(profile);
		
		// Log profile update
		auditLogService.log("RECRUITER_PROFILE", profile.getId(), "UPDATED", userId);
	}


}
