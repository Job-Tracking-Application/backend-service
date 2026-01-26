package com.jobtracking.application.service;

import java.util.List;
import com.jobtracking.application.dto.CandidateApplicationResponse;
import com.jobtracking.application.dto.ApplicationResponse;
import com.jobtracking.application.dto.UpdateStatusRequest;
import com.jobtracking.application.dto.ApplyJobRequest;

public interface ApplicationService {

    List<CandidateApplicationResponse> getCandidateApplication(Long userId);

    List<ApplicationResponse> getApplication(Long jobId);

    ApplicationResponse updateApplication(Long id, UpdateStatusRequest updateStatusRequest);

    void createApplication(Long jobId, Long userId, ApplyJobRequest application);
    
    boolean hasUserAppliedForJob(Long jobId, Long userId);
}