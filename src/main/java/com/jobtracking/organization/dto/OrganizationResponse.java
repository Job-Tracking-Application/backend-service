package com.jobtracking.organization.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrganizationResponse {

    private Long id;
    private String name;
    private String website;
    private String city;
    private String contactEmail;
    private Boolean verified;
    private String extension;
    private LocalDateTime createdAt;
}
