
package com.jobtracking.application.dto;

public record ApplyJobRequest(
        String resume, // optional (URL or path)
        String coverLetter) {

}