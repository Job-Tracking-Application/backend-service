package com.jobtracking.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobtracking.application.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {}