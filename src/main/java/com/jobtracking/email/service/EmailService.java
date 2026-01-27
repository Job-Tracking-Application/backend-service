package com.jobtracking.email.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    @Value("${mailersend.api.key}")
    private String apiKey;

    @Value("${mailersend.from.email}")
    private String fromEmail;

    @Value("${mailersend.from.name}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Async + HTML email
     */
    @Async
    public void sendStatusUpdateMail(String toEmail, String jobTitle, String status) {
        
        String url = "https://api.mailersend.com/v1/email";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String htmlBody = """
        <html>
        <body style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px;">
          <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 6px;">
            <h2 style="color: #333;">Application Status Updated</h2>
            <p>Hello,</p>
            <p>Your application status has been updated.</p>
            
            <table style="margin-top: 15px;">
              <tr>
                <td><strong>Job Title:</strong></td>
                <td>%s</td>
              </tr>
              <tr>
                <td><strong>Current Status:</strong></td>
                <td style="color: #1a73e8;"><strong>%s</strong></td>
              </tr>
            </table>
            
            <p style="margin-top: 20px;">
              Regards,<br/>
              <strong>%s</strong>
            </p>
          </div>
        </body>
        </html>
        """.formatted(jobTitle, status, fromName);

        String body = """
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
          "subject": "Application Status Updated",
          "html": %s
        }
        """.formatted(
                fromEmail,
                fromName,
                toEmail,
                toEmail,
                escapeJson(htmlBody)
        );

        try {
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, entity, String.class);
            System.out.println("HTML email sent async to: " + toEmail);
            System.out.println("Thread: " + Thread.currentThread().getName());
        } catch (Exception e) {
            System.err.println("MailerSend email failed: " + e.getMessage());
        }
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