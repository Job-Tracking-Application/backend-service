package com.jobtracking.profile.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jobtracking.auth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recruiter_profile")
@Getter
@Setter
public class RecruiterProfile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
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
	
	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
