package com.jobtracking.job.entity;

import com.jobtracking.organization.entity.Organization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(length = 100)
    private String location;

    private Double salary;

    @Column(name = "job_type", length = 50)
    private String jobType;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Organization company;

    @Column(name = "recruiter_user_id", nullable = false)
    private Long recruiterUserId;

    @Column(length = 50)
    private String status;

    private LocalDate deadline;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
