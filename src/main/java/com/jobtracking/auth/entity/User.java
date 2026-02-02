package com.jobtracking.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import com.jobtracking.common.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

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

    @Column(name = "language_pref", length = 2)
    private String languagePref;

    @Column(name = "sensitive_info")
    private byte[] sensitiveInfo;

    @Column(columnDefinition = "json")
    private String extension;
    
    @Column(name = "phone_no")
    private String phone;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Override PrePersist to handle Builder defaults
    @PrePersist
    protected void onCreate() {
        super.onCreate(); // Call parent method for createdAt/updatedAt
        if (active == null) {
            active = true;
        }
    }
}
