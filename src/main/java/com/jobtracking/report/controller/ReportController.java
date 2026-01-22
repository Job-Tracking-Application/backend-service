package com.jobtracking.report.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
	@GetMapping("/summary")
	public Map<String, Object> summary() {
		return Map.of("message", "Summary report placeholder", "status", "COMING_SOON");
	}

	@GetMapping("/matrix")
	public Map<String, Object> matrix() {
		return Map.of("message", "Matrix report placeholder", "status", "COMING_SOON");
	}
}