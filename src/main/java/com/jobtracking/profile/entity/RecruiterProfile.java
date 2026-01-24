package com.jobtracking.profile.entity;

import com.jobtracking.auth.entity.User;
import com.jobtracking.organization.entity.Organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "recruiter_profile")
public class RecruiterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key mapping to User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // Foreign Key mapping to Organization (Company)
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Organization company;
    // Company description
    @Column(name = "company_desc", columnDefinition = "TEXT")
    private String companyDesc;
    // Verification status
    @Column(name = "verified", nullable = false)
    private boolean verified;
}
