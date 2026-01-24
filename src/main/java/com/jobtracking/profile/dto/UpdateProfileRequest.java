package com.jobtracking.profile.dto;

import java.util.List;

public record UpdateProfileRequest(
                String fullName,
                String email,
                String userName,
                List<String> skills,
                String resume,
                String about,
                EducationDTO education) {
}
