package com.jobtracking.report.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracking.report.service.ReportService;

@RestController
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
	
	private final ReportService reportService;
	
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping("/summary")
	public Map<String, Object> summary() {
		return reportService.getSummaryReport();
	}

	@GetMapping("/matrix")
	public Map<String, Object> matrix() {
		return reportService.getMatrixReport();
	}
}