package com.jobtracking.organization.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.jobtracking.common.entity.BaseEntity;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Organization extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String website;
    
    private String city;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    private Boolean verified = false;
    
    @Column(name = "recruiter_user_id")
    private Long recruiterUserId;

    @Column(columnDefinition = "json")
    private String extension;
}
