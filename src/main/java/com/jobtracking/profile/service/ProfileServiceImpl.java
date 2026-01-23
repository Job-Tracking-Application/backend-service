package com.jobtracking.profile.service;

import java.util.List;

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
		JobSeekerProfile profile = jobSeekerProfileRepo.findByUserId(id)
				.orElseThrow(() -> new RuntimeException("Profile Not found"));
		ProfileResponse dto = new ProfileResponse();
		dto.setFullName(user.getFullname());
		dto.setEmail(user.getEmail());
		dto.setUserName(user.getUsername());
		dto.setSkills(jobSeekerSkills.findByJobSeekerProfile(profile).stream()
				.map(jsSkill -> jsSkill.getSkill().getName()).toList());
		dto.setResume(profile.getResumeLink());
		dto.setAbout(profile.getBioEn());
		dto.setEducation(profile.getEducation());
		return dto;
	}

	@Override
	public void updateJobSeekerProfile(Long userId, UpdateProfileRequest req) {
		// TODO Auto-generated method stub
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
		jobSeekerProfileRepo.save(profile);
		jobSeekerSkills.deleteByJobSeekerProfileId(profile.getId());
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
