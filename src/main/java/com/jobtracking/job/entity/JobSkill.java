package com.jobtracking.job.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "job_skills")
@Getter
@Setter
public class JobSkill {

    @EmbeddedId
    private JobSkillId id;
}
