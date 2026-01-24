package com.jobtracking.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // corresponds to `username`
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    // corresponds to `password_hash`
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // FK to roles table
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(length = 200)
    private String fullname;

    @Column(name = "language_pref")
    private String languagePref; // en / mr

    @Lob
    @Column(name = "sensitive_info")
    private byte[] sensitiveInfo;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "json")
    private String extension;
    
    @Column(name = "phone_no")
    private String phone;
}
