package com.jobtracking.common.utils;

public class RoleMapper {
	private RoleMapper() {
	}

	public static String toAuthority(Integer roleId) {
		return switch (roleId) {
		case 1 -> "ADMIN";
		case 2 -> "RECRUITER";
		case 3 -> "JOB_SEEKER";
		default -> "UNKNOWN";
		};
	}
}
