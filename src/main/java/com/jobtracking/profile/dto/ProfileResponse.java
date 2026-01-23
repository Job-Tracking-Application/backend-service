package com.jobtracking.profile.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileResponse {
    private String fullName;
    private String email;
    private String userName;
    private List<String> skills;
    private String resume;
    private String about;
    private String education;
}
