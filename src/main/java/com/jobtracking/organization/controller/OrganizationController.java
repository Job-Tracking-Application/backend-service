package com.jobtracking.organization.controller;

import com.jobtracking.organization.dto.OrganizationRequest;
import com.jobtracking.organization.dto.OrganizationResponse;
import com.jobtracking.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    
    @GetMapping("/{id}")
    public OrganizationResponse getCompany(@PathVariable Long id) {
        return organizationService.getById(id);
    }

    
    @PostMapping
    public OrganizationResponse createCompany(@RequestBody OrganizationRequest request) {
        return organizationService.create(request);
    }

    
    @PutMapping("/{id}")
    public OrganizationResponse updateCompany(
            @PathVariable Long id,
            @RequestBody OrganizationRequest request) {
        return organizationService.update(id, request);
    }
}
