package com.jobtracking.profile.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.jobtracking.job.entity.Job;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "skills")
@NoArgsConstructor   // REQUIRED for JPA
@Getter
@Setter
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false, unique = true)
    private String name;

    // Many-to-Many relationship with Jobs through job_skills table
    // Use @JsonIgnore to prevent circular reference during JSON serialization
    @JsonIgnore
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private List<Job> jobs;

    //constructor 
    public Skill(String name) {
        this.name = name;
    }
}
