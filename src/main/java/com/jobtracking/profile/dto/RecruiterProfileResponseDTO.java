package com.jobtracking.profile.dto;

public record RecruiterProfileResponseDTO(
        String fullName,
        String email,
        String userName,
        String phone,
        String companyName,
        String companyDesc,
        boolean verified) {

}
