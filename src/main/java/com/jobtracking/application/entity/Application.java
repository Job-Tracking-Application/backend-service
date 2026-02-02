package com.jobtracking.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.jobtracking.application.enums.ApplicationStatus;
import com.jobtracking.auth.entity.User;
import com.jobtracking.job.entity.Job;
import com.jobtracking.common.entity.SoftDeleteEntity;

@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = { "job_id", "seeker_id" }))
@Getter
@Setter
public class Application extends SoftDeleteEntity {

    @Column(name = "resume_path", length = 500)
    private String resumePath;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "recruiter_notes", columnDefinition = "TEXT")
    private String recruiterNotes;

    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "completion_datetime")
    private LocalDateTime completionDatetime;

    @Column(columnDefinition = "json")
    private String extension;

    /* ===== Relations ===== */
    @ManyToOne
    @JoinColumn(name = "seeker_id", nullable = false)
    private User user; // applicant

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @PrePersist
    protected void onApply() {
        super.onCreate(); // Call parent method for createdAt/updatedAt
        appliedAt = LocalDateTime.now();
        if (status == null) {
            status = ApplicationStatus.APPLIED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        super.onUpdate(); // Call parent method for updatedAt
    }
}
