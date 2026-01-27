package com.jobtracking.application.enums;

public enum ApplicationStatus {
	APPLIED,
	UNDER_REVIEW,
	INTERVIEWED,
	SHORTLISTED, // Keep for backward compatibility with existing data
	REJECTED,
	HIRED,
	PENDING
}
