package com.jobtracking.profile.service;

import org.springframework.stereotype.Service;

import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
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

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	private final JobSeekerProfileRepository jobSeekerProfileRepo;
	private final UserRepository userRepo;
	private final JobSeekerSkillsRepository jobSeekerSkills;
	private final SkillRepository skillRepo;

	public ProfileResponse getJobSeekerProfile(Long id) {
		User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("user Not found"));
		
		// Get or create profile if it doesn't exist
		JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(id)
				.orElseGet(() -> {
					// Create a new profile if it doesn't exist
					JobSeekerProfile newProfile = new JobSeekerProfile();
					newProfile.setUser(user);
					return jobSeekerProfileRepo.save(newProfile);
				});
		
		ProfileResponse dto = new ProfileResponse();
		dto.setFullName(user.getFullname());
		dto.setEmail(user.getEmail());
		dto.setUserName(user.getUsername());
		
		// Fetch skills with error handling
		try {
			dto.setSkills(jobSeekerSkills.findByJobSeekerProfile(profile).stream()
					.map(jsSkill -> jsSkill.getSkill().getName()).toList());
		} catch (Exception e) {
			System.err.println("Error fetching skills for user " + id + ": " + e.getMessage());
			dto.setSkills(java.util.List.of()); // Set empty list on error
		}
		
		dto.setResume(profile.getResumeLink());
		dto.setAbout(profile.getBioEn());
		dto.setEducation(profile.getEducation());
		
		// Debug: Log the profile data being returned
		System.out.println("Profile data for user " + id + ":");
		System.out.println("  Education: " + profile.getEducation());
		System.out.println("  About: " + profile.getBioEn());
		System.out.println("  Resume: " + profile.getResumeLink());
		System.out.println("  Skills count: " + dto.getSkills().size());
		
		return dto;
	}

	@Override
	public void updateJobSeekerProfile(Long userId, UpdateProfileRequest req) {
		User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("user Not found"));
		user.setFullname(req.fullName());
		user.setUsername(req.userName());
		userRepo.save(user);

		JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(userId).orElseGet(() -> {
			JobSeekerProfile p = new JobSeekerProfile();
			p.setUser(user);
			return p;
		});
		profile.setBioEn(req.about());
		profile.setEducation(req.education());
		profile.setResumeLink(req.resume());
		
		// Save profile first to ensure it has an ID
		profile = jobSeekerProfileRepo.save(profile);
		
		// Delete existing skills and add new ones
		jobSeekerSkills.deleteByJobSeekerProfileId(profile.getId());
		
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

	@Override
	public void createDemoSkillsForUser(Long userId) {
		User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		
		// Get or create profile
		JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(userId)
				.orElseGet(() -> {
					JobSeekerProfile newProfile = new JobSeekerProfile();
					newProfile.setUser(user);
					newProfile.setBioEn("Experienced software developer with passion for creating innovative solutions");
					newProfile.setEducation("Bachelor's in Computer Science");
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
