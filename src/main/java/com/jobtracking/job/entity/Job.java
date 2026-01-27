package com.jobtracking.job.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.jobtracking.profile.entity.Skill;
import com.jobtracking.common.entity.SoftDeleteEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job extends SoftDeleteEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String location;

    @Column(name = "min_salary")
    private Double minSalary;

    @Column(name = "max_salary")
    private Double maxSalary;

    @Column(name = "min_experience")
    private Integer minExperience;

    @Column(name = "max_experience")
    private Integer maxExperience;

    @Column(name = "job_type", length = 50)
    private String jobType;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "recruiter_id", nullable = false)
    private Long recruiterUserId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "expiry_date")
    private LocalDateTime deadline;

    @Column(columnDefinition = "json")
    private String extension;

    // Many-to-Many relationship with Skills through job_skills table
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_skills",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;

    @PrePersist
    protected void onCreate() {
        super.onCreate(); // Call parent method for createdAt/updatedAt
        if (postedAt == null) {
            postedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        super.onUpdate(); // Call parent method for updatedAt
    }
}
