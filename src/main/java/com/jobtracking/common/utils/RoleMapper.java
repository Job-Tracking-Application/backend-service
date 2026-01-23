package com.jobtracking.common.utils;

public class RoleMapper {

    public static String mapRoleIdToRoleName(Integer roleId) {
        return switch (roleId) {
            case 1 -> "ADMIN";
            case 2 -> "RECRUITER";
            case 3 -> "JOB_SEEKER";
            default -> throw new IllegalArgumentException("Invalid roleId");
        };
    }
}
