package com.jobtracking.profile.entity;

import com.jobtracking.profile.enums.Proficiency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "jobseeker_skills")
public class JobSeekerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → jobseeker_profile
    @ManyToOne
    @JoinColumn(name = "seeker_id", nullable = false)
    private JobSeekerProfile jobSeekerProfile;

    // FK → skills
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(length = 50,nullable = false)
    private Proficiency proficiency; // BEGINNER / INTERMEDIATE / EXPERT
}
