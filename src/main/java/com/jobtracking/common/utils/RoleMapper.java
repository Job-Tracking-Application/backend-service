package com.jobtracking.common.utils;

public class RoleMapper {
	private RoleMapper() {
	}

	public static String toAuthority(Integer roleId) {
		if (roleId == null) return "ROLE_ANONYMOUS";
		return switch (roleId) {
		case 1 -> "ROLE_ADMIN";
		case 2 -> "ROLE_RECRUITER";
		case 3 -> "ROLE_JOB_SEEKER";
		default -> "ROLE_UNKNOWN";
		};
	}
}
