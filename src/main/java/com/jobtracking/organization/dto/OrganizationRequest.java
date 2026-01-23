package com.jobtracking.organization.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationRequest {

    private String name;
    private String website;
    private String city;
    private String contactEmail;
    private String extension; // JSON string
}
