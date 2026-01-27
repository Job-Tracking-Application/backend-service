package com.jobtracking.email.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jobtracking.application.enums.ApplicationStatus;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${mailersend.api.key}")
    private String apiKey;

    @Value("${mailersend.from.email}")
    private String fromEmail;

    @Value("${mailersend.from.name}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send async HTML email for application status updates
     */
    @Async
    public void sendStatusUpdateMail(String toEmail, String jobTitle, String status) {
        
        // Validate inputs
        if (!isValidEmail(toEmail)) {
            logger.warn("Invalid email address provided: {}", toEmail);
            return;
        }
        
        if (jobTitle == null || jobTitle.trim().isEmpty()) {
            logger.warn("Invalid job title provided for email to: {}", toEmail);
            return;
        }
        
        if (status == null || status.trim().isEmpty()) {
            logger.warn("Invalid status provided for email to: {}", toEmail);
            return;
        }

        try {
            String displayStatus = getStatusDisplayName(status);
            String htmlBody = buildEmailTemplate(jobTitle, displayStatus);
            
            String requestBody = buildEmailRequest(toEmail, htmlBody);
            
            ResponseEntity<String> response = sendEmailRequest(requestBody);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Email sent successfully to: {} for job: '{}' with status: {}", 
                           toEmail, jobTitle, displayStatus);
                logger.debug("Email sent on thread: {}", Thread.currentThread().getName());
            } else {
                logger.warn("Email sending returned non-success status: {} for email: {}", 
                           response.getStatusCode(), toEmail);
            }
            
        } catch (Exception e) {
            logger.error("Failed to send status update email to: {} for job: '{}' with status: {}", 
                        toEmail, jobTitle, status, e);
        }
    }

    /**
     * Validate email address format
     */
    private boolean isValidEmail(String email) {
        return email != null && 
               email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") &&
               email.length() <= 254; // RFC 5321 limit
    }

    /**
     * Convert status enum to user-friendly display name
     */
    private String getStatusDisplayName(String status) {
        try {
            ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
            return switch (appStatus) {
                case APPLIED -> "Applied";
                case UNDER_REVIEW -> "Under Review";
                case INTERVIEWED -> "Interviewed";
                case SHORTLISTED -> "Shortlisted";
                case HIRED -> "Hired";
                case REJECTED -> "Rejected";
                case PENDING -> "Pending";
            };
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown status provided: {}, using as-is", status);
            return status;
        }
    }

    /**
     * Build HTML email template
     */
    private String buildEmailTemplate(String jobTitle, String displayStatus) {
        String statusColor = getStatusColor(displayStatus);
        
        return """
        <html>
        <body style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px; margin: 0;">
          <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
            <div style="text-align: center; margin-bottom: 30px;">
              <h1 style="color: #333; margin: 0; font-size: 24px;">Application Status Update</h1>
            </div>
            
            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 6px; margin-bottom: 20px;">
              <p style="margin: 0 0 10px 0; color: #666; font-size: 16px;">Hello,</p>
              <p style="margin: 0 0 20px 0; color: #666; font-size: 16px;">Your job application status has been updated.</p>
              
              <table style="width: 100%%; border-collapse: collapse;">
                <tr>
                  <td style="padding: 10px 0; border-bottom: 1px solid #eee;"><strong style="color: #333;">Job Title:</strong></td>
                  <td style="padding: 10px 0; border-bottom: 1px solid #eee; color: #333;">%s</td>
                </tr>
                <tr>
                  <td style="padding: 10px 0;"><strong style="color: #333;">Current Status:</strong></td>
                  <td style="padding: 10px 0;">
                    <span style="background-color: %s; color: white; padding: 6px 12px; border-radius: 4px; font-weight: bold; font-size: 14px;">
                      %s
                    </span>
                  </td>
                </tr>
              </table>
            </div>
            
            <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;">
              <p style="margin: 0; color: #666; font-size: 14px;">
                Best regards,<br/>
                <strong style="color: #333;">%s</strong>
              </p>
            </div>
          </div>
        </body>
        </html>
        """.formatted(jobTitle, statusColor, displayStatus, fromName);
    }

    /**
     * Get color for status badge
     */
    private String getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "hired" -> "#28a745";
            case "rejected" -> "#dc3545";
            case "interviewed" -> "#17a2b8";
            case "shortlisted" -> "#28a745";
            case "under review" -> "#ffc107";
            case "applied" -> "#007bff";
            case "pending" -> "#6c757d";
            default -> "#007bff";
        };
    }

    /**
     * Build email request JSON
     */
    private String buildEmailRequest(String toEmail, String htmlBody) {
        return """
        {
          "from": {
            "email": "%s",
            "name": "%s"
          },
          "to": [
            {
              "email": "%s",
              "name": "%s"
            }
          ],
          "subject": "Application Status Update - %s",
          "html": %s
        }
        """.formatted(
                fromEmail,
                fromName,
                toEmail,
                toEmail.split("@")[0], // Use email prefix as name
                fromName,
                escapeJson(htmlBody)
        );
    }

    /**
     * Send email request to MailerSend API
     */
    private ResponseEntity<String> sendEmailRequest(String requestBody) {
        String url = "https://api.mailersend.com/v1/email";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForEntity(url, entity, String.class);
    }

    /**
     * Escape HTML string for JSON
     */
    private String escapeJson(String value) {
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "") + "\"";
    }
}