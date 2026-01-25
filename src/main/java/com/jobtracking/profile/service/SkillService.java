package com.jobtracking.profile.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtracking.profile.entity.Skill;
import com.jobtracking.profile.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill createSkill(String skillName) {
        return skillRepository.findByName(skillName)
            .orElseGet(() -> skillRepository.save(new Skill(skillName)));
    }
}