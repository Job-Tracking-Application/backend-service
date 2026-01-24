# Job Tracking Backend Service

## Overview
Backend service for the Job Tracking Application with comprehensive job management, profile management, and application tracking features.

## Features
- Job Management (CRUD operations with soft delete)
- User Profile Management (Job Seekers and Recruiters)
- Application Management
- Dashboard Statistics
- Role-based Authentication and Authorization
- Audit Logging

## API Documentation

### 1. Get Job Seeker Profile
**Endpoint:** `GET /api/profile/jobseeker`

#### Response Example
```json
{
  "fullName": "Tushar Patil",
  "email": "tushar@gmail.com",
  "userName": "tushar",
  "skills": ["Java", "Spring Boot"],
  "resume": "resume.pdf",
  "about": "Backend Developer",
  "education": "{\"degree\":\"BTech\",\"college\":\"ABC\",\"year\":2024}"
}
```

> **⚠️ Note:** `education` is returned as a JSON string, not an object. Frontend must `JSON.parse(education)` before using it.

---

### 2. Update Job Seeker Profile
**Endpoint:** `PUT /api/profile/jobseeker`

#### Request Body Format
```json
{
  "fullName": "Tushar Patil",
  "email": "tushar@gmail.com",
  "userName": "tushar",
  "skills": ["Java", "Spring Boot", "MySQL"],
  "resume": "resume_v2.pdf",
  "about": "Spring Boot Developer",
  "education": "{\"degree\":\"BTech\",\"college\":\"ABC\",\"year\":2024}"
}
```

### ✅ Important Rules
- `education` must be sent as a **STRING**.
- Do not send `education` as a JSON array or object.
- `skills` must be an array of strings.

---

## 3. Apply for a Job
**Endpoint:** `POST /api/application/{jobId}`

### Request Body Format
```json
{
  "resume": "resume_v1.pdf"
}
```
*(Optional: `coverLetter` can also be included)*

### ✅ Rules Enforced by Backend
- Only `JOB_SEEKER` users can apply.
- One application per job per user.
- Duplicate applications are blocked at service + DB level.

---

## 4. Get My Applications (Candidate View)
**Endpoint:** `GET /api/application/my`

### Response Example
```json
[
  {
    "applicationId": 12,
    "jobTitle": "Java Backend Developer",
    "company": "ABC Tech",
    "status": "APPLIED",
    "appliedDate": "2026-01-23",
    "resumePath": "resume_v1.pdf"
  }
]
```

### ✅ Notes
- This endpoint is only for logged-in job seekers.
- Used for "My Applications" page.

---

## 5. Get Applications for a Job (Recruiter / Admin)
**Endpoint:** `GET /api/application/manage/{jobId}`

### Path Variable
| Name | Type | Description |
|------|------|-------------|
| `jobId` | number | ID of the job |

### Response Example
```json
[
  {
    "applicationId": 12,
    "candidateName": "Tushar Patil",
    "email": "tushar@gmail.com",
    "skills": ["Java", "Spring Boot"],
    "status": "APPLIED",
    "resumePath": "resume_v1.pdf"
  }
]
```

### ✅ Notes
- Used on Manage Applications screen.
- Shows candidate skills from profile.

---

## 6. Update Application Status
**Endpoint:** `PATCH /api/application/manage/{applicationId}`

### Request Body
```json
{
  "status": "SHORTLISTED"
}
```
**Valid Statuses:** `APPLIED`, `SHORTLISTED`, `REJECTED`, `INTERVIEW_SCHEDULED`, `HIRED`.