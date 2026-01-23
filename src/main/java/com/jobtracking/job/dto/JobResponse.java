package com.jobtracking.job.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String location;

    private Double minSalary;
    private Double maxSalary;

    private Integer minExperience;
    private Integer maxExperience;

    private String jobType;

    private Long companyId;
    private Long recruiterUserId;

    private Boolean isActive;

    private LocalDateTime postedAt;
    private LocalDateTime deadline;

    private List<String> skills;
}
