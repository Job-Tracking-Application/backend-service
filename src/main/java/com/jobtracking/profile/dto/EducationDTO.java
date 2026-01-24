package com.jobtracking.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public record EducationDTO(  
		String degree, String college,
int year) {
 
}
