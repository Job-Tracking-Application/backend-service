package com.jobtracking.job.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobListResponse {

    private Long id;
    private String title;
    private String location;
    private String jobType;

    private Double minSalary;
    private Double maxSalary;

    private Long companyId;
}
