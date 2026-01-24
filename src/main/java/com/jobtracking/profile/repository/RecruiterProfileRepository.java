package com.jobtracking.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobtracking.profile.entity.RecruiterProfile;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
    Optional<RecruiterProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}