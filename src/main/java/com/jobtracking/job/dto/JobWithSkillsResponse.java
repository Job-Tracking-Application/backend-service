package com.jobtracking.job.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobWithSkillsResponse {
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
    private String companyName;
    private Long recruiterUserId;
    private Boolean isActive;
    private LocalDateTime postedAt;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Skills information
    private List<SkillInfo> skills;
    
    @Getter
    @Setter
    public static class SkillInfo {
        private Long id;
        private String name;
        
        public SkillInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}