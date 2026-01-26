package com.jobtracking.profile.dto;

public record RecruiterUpdateProfile(
        String fullName,
        String userName,
        String phone,
        String companyName,
        String companyDesc) {

}
