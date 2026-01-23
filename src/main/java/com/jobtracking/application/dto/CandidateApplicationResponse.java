package com.jobtracking.application.dto;

import java.time.LocalDate;

public record CandidateApplicationResponse(
    Long id,
    String JobTitle,
    String Company,
    String Status,
    LocalDate appliedDate,
    String resume    
) {  
}
