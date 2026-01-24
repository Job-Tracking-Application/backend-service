package com.jobtracking.profile.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracking.auth.entity.User;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.profile.dto.EducationDTO;
import com.jobtracking.profile.dto.ProfileResponse;
import com.jobtracking.profile.dto.RecruiterProfileResponseDTO;
import com.jobtracking.profile.dto.RecruiterUpdateProfile;
import com.jobtracking.profile.dto.UpdateProfileRequest;
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

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	private final JobSeekerProfileRepository jobSeekerProfileRepo;
	private final RecruiterProfileRepository recruiterProfileRepo;
	private final UserRepository userRepo;
	private final JobSeekerSkillsRepository jobSeekerSkills;
	private final SkillRepository skillRepo;
	private final OrganizationRepository organizationRepo;

	public ProfileResponse getJobSeekerProfile(Long id) {

		User user = userRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found"));

		JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(id)
				.orElseThrow(() -> new RuntimeException("Profile not found"));

		// Skills
		List<String> skills = jobSeekerSkills.findByJobSeekerProfile(profile)
				.stream()
				.map(jsSkill -> jsSkill.getSkill().getName())
				.toList();

		// Education (JSON string → Object)
		EducationDTO education = null;
		if (profile.getEducation() != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				education = mapper.readValue(profile.getEducation(), EducationDTO.class);
			} catch (Exception e) {
				education = null;
			}
		}
		// ✅ RETURN record using constructor
		return new ProfileResponse(
				user.getFullname(),
				user.getEmail(),
				user.getUsername(),
				skills,
				profile.getResumeLink(),
				profile.getBioEn(),
				education);
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

		// 3️⃣ ✅ Convert EducationDTO → JSON string
		if (req.education() != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				profile.setEducation(mapper.writeValueAsString(req.education()));
			} catch (Exception e) {
				throw new RuntimeException("Invalid education data");
			}
		}

		jobSeekerProfileRepo.save(profile);

		// 4️⃣ Replace skills (delete + insert)
		jobSeekerSkills.deleteByJobSeekerProfileId(profile.getId());

		if (req.skills() != null) {
			for (String skillName : req.skills()) {

				Skill skill = skillRepo.findByName(skillName)
						.orElseGet(() -> {
							Skill s = new Skill();
							s.setName(skillName);
							return skillRepo.save(s);
						});

				JobSeekerSkill js = new JobSeekerSkill();
				js.setJobSeekerProfile(profile);
				js.setSkill(skill);
				js.setProficiency(Proficiency.BEGINNER); // default
				jobSeekerSkills.save(js);
			}
		}
	}

	@Override
	public RecruiterProfileResponseDTO getRecruiterProfile(Long userId) {
		// Fetch user
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Fetch recruiter profile
		RecruiterProfile profile = recruiterProfileRepo.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
		// Return DTO with all required information
		return new RecruiterProfileResponseDTO(
				user.getFullname(),
				user.getEmail(),
				user.getUsername(),
				profile.getCompany().getName(),
				profile.getCompanyDesc(),
				profile.isVerified());
	}

	@Override
	public void updateRecruiterProfile(Long userId, RecruiterUpdateProfile request) {
		// Fetch user
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		user.setFullname(request.fullName());
		user.setUsername(request.userName());
		userRepo.save(user);
		// Fetch recruiter profile
		RecruiterProfile profile = recruiterProfileRepo.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

		// Update company if null or if company name is provided in request
		if (profile.getCompany() == null && request.companyName() != null) {
			// Search for organization by name
			Organization organization = organizationRepo.findByName(request.companyName())
					.orElseThrow(
							() -> new RuntimeException("Organization not found with name: " + request.companyName()));
			profile.setCompany(organization);
		}

		// Update company description
		profile.setCompanyDesc(request.companyDesc());
		recruiterProfileRepo.save(profile);
	}

}
