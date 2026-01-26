package com.jobtracking.dashboard.dto;

public record DashboardStatsResponse(
        Long activeJobs,
        Long pendingApplications,
        Long hiredCandidates
) {
}