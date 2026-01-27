package com.jobtracking.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobtracking.auth.entity.User;
import com.jobtracking.common.repository.BaseRepository;

@Repository
public interface UserRepository extends BaseRepository<User> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Find active users only
    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.createdAt DESC")
    List<User> findAllActive();
    
    // Find users by role
    @Query("SELECT u FROM User u WHERE u.roleId = :roleId ORDER BY u.createdAt DESC")
    List<User> findByRoleId(@Param("roleId") Integer roleId);
    
    // Find active users by role
    @Query("SELECT u FROM User u WHERE u.roleId = :roleId AND u.active = true ORDER BY u.createdAt DESC")
    List<User> findByRoleIdAndActive(@Param("roleId") Integer roleId);
    
    // Count users by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.roleId = :roleId")
    long countByRoleId(@Param("roleId") Integer roleId);
    
    // Count active users
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countByActiveTrue();
    
    // Count inactive users
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = false")
    long countByActiveFalse();
    
    // Find users by language preference
    @Query("SELECT u FROM User u WHERE u.languagePref = :languagePref ORDER BY u.createdAt DESC")
    List<User> findByLanguagePref(@Param("languagePref") String languagePref);
}
