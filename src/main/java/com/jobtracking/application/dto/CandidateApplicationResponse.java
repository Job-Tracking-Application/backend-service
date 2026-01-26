package com.jobtracking.application.dto;

import java.time.LocalDate;

public record CandidateApplicationResponse(
    Long id,
    String jobTitle,
    String company,
    String status,
    LocalDate appliedDate,
    String resume    
) {  
}
