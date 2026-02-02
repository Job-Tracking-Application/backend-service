package com.jobtracking.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.profile.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByName(String skillName);
    
    boolean existsByName(String skillName);
    
    // Find skills by name pattern (case insensitive)
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :pattern, '%')) ORDER BY s.name ASC")
    List<Skill> findByNameContainingIgnoreCase(@Param("pattern") String pattern);
    
    // Find all skills ordered by name
    @Query("SELECT s FROM Skill s ORDER BY s.name ASC")
    List<Skill> findAllOrderByName();
    
    // Find skills by exact name (case insensitive)
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) = LOWER(:name)")
    Optional<Skill> findByNameIgnoreCase(@Param("name") String name);
    
    // Check if skill name exists (case insensitive)
    @Query("SELECT COUNT(s) > 0 FROM Skill s WHERE LOWER(s.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
    
    // Find most popular skills (skills with most job associations)
    @Query("""
        SELECT s FROM Skill s 
        JOIN s.jobs j 
        GROUP BY s.id 
        ORDER BY COUNT(j) DESC
    """)
    List<Skill> findMostPopularSkills();
}
