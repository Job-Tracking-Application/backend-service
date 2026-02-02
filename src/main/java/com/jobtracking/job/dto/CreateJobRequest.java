package com.jobtracking.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateJobRequest {

    @NotBlank
    private String title;

    private String description;

    private String location;

    @NotNull
    private Double minSalary;

    @NotNull
    private Double maxSalary;

    private Integer minExperience;
    private Integer maxExperience;

    @NotBlank
    private String jobType;

    @NotNull
    private Long companyId;

    private LocalDateTime deadline;

    // Skill IDs selected from dropdown
    private List<Long> skillIds;
}
