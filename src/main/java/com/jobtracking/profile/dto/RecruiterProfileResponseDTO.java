package com.jobtracking.profile.dto;

public record RecruiterProfileResponseDTO(
        String fullName,
        String email,
        String userName,
        String companyName,
        String companyDesc,
        boolean verified) {

}
