package com.jobtracking.profile.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


public record ProfileResponse(   
		String fullName,
     String email,
     String userName,
    List<String> skills,
    String resume,
    String about,
    EducationDTO education) {
 
}
