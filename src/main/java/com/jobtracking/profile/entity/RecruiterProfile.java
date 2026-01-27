package com.jobtracking.profile.entity;

import com.jobtracking.auth.entity.User;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recruiter_profile")
@Getter
@Setter
public class RecruiterProfile extends BaseEntity {
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	// Personal profile fields
	@Column(name = "bio_en", columnDefinition = "TEXT")
	private String bioEn;
	
	@Column(name = "bio_mr", columnDefinition = "TEXT")
	private String bioMr;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "linkedin_url")
	private String linkedinUrl;
	
	@Column(name = "years_experience")
	private Integer yearsExperience;
	
	@Column(name = "specialization")
	private String specialization;
	
	// Company-related fields (optional - can be null if recruiter doesn't have company)
	@ManyToOne
	@JoinColumn(name = "company_id")
	private Organization company;
	
	@Column(name = "company_desc", columnDefinition = "TEXT")
	private String companyDesc;
	
	@Column(name = "verified", nullable = false)
	private boolean verified = false;
}
