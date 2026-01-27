package com.jobtracking.profile.entity;

import java.util.List;

import com.jobtracking.auth.entity.User;
import com.jobtracking.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name ="jobseeker_profile")
public class JobSeekerProfile extends BaseEntity {

    @Column(name = "resume_link", length = 500)
    private String resumeLink;

    @Column(name = "bio_en", columnDefinition = "TEXT")
    private String bioEn;

    @Column(name = "bio_mr", columnDefinition = "TEXT")
    private String bioMr;
    
    // Education and experience as TEXT fields
    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(columnDefinition = "TEXT")
    private String experience;
  
    // 🔹 Foreign Key mapping
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobSeekerSkill> skills;
}
