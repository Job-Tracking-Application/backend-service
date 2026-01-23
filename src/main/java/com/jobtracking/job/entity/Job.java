package com.jobtracking.job.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "expiry_date")
    private LocalDateTime deadline;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "json")
    private String extension;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (postedAt == null) {
            postedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
