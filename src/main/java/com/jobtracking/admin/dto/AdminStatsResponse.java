package com.jobtracking.admin.dto;

public record AdminStatsResponse(
	    long totalUsers,
	    long totalJobs,
	    long totalCompanies,
	    long totalApplications
	) {}