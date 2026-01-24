package com.jobtracking.profile.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.profile.dto.EducationDTO;
import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.UpdateProfileRequest;
import com.jobtracking.profile.entity.JobSeekerProfile;
import com.jobtracking.profile.entity.JobSeekerSkill;
import com.jobtracking.profile.entity.Skill;
import com.jobtracking.profile.enums.Proficiency;
import com.jobtracking.profile.repository.JobSeekerProfileRepository;
import com.jobtracking.profile.repository.JobSeekerSkillsRepository;
import com.jobtracking.profile.repository.SkillRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	private final JobSeekerProfileRepository jobSeekerProfileRepo;
	private final UserRepository userRepo;
	private final JobSeekerSkillsRepository jobSeekerSkills;
	private final SkillRepository skillRepo;

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
			System.err.println("Error fetching skills for user " + id + ": " + e.getMessage());
			skills = java.util.List.of(); // Set empty list on error
		}
		
		// Education (JSON string → Object)
		EducationDTO education = null;
		if (profile.getEducation() != null && !profile.getEducation().trim().isEmpty()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				education = mapper.readValue(profile.getEducation(), EducationDTO.class);
			} catch (Exception e) {
				System.err.println("Error parsing education JSON for user " + id + ": " + e.getMessage());
				education = null;
			}
		}
		
		// Debug: Log the profile data being returned
		System.out.println("Profile data for user " + id + ":");
		System.out.println("  Education: " + profile.getEducation());
		System.out.println("  About: " + profile.getBioEn());
		System.out.println("  Resume: " + profile.getResumeLink());
		System.out.println("  Skills count: " + skills.size());
		
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
		user.setUsername(req.userName());
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

		// 3️⃣ Convert EducationDTO → JSON string
		if (req.education() != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				profile.setEducation(mapper.writeValueAsString(req.education()));
			} catch (Exception e) {
				throw new RuntimeException("Invalid education data");
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
	}

	@Override
	public void createDemoSkillsForUser(Long userId) {
		User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		
		// Get or create profile
		JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(userId)
				.orElseGet(() -> {
					JobSeekerProfile newProfile = new JobSeekerProfile();
					newProfile.setUser(user);
					newProfile.setBioEn("Experienced software developer with passion for creating innovative solutions");
					newProfile.setEducation("{\"degree\":\"Bachelor's in Computer Science\",\"college\":\"Tech University\",\"year\":2020}");
					newProfile.setResumeLink("https://drive.google.com/file/d/demo-resume-link");
					return jobSeekerProfileRepo.save(newProfile);
				});

		// Clear existing skills first
		jobSeekerSkills.deleteByJobSeekerProfileId(profile.getId());

		// Create demo skills
		String[] skillNames = {"Java", "Spring Boot", "React", "JavaScript", "MySQL", "Git", "REST APIs", "HTML/CSS"};
		
		for (String skillName : skillNames) {
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
				js.setProficiency(Proficiency.INTERMEDIATE);
				jobSeekerSkills.save(js);
			}
		}
	}
}
