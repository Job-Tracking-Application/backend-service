# Job Tracking Backend Service

A comprehensive Spring Boot REST API for job tracking application with role-based authentication, job management, and application tracking. Features enhanced error handling, company verification system, and robust profile management.

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Job-Tracking-Application/backend-service.git
   cd backend-service
   ```

2. **Configure Database**
   ```properties
   # src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/job_tracking2
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**
   - Base URL: `http://localhost:5000/api`
   - Swagger UI: `http://localhost:5000/swagger-ui.html`
   - Health Check: `http://localhost:5000/api/actuator/health`

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.1
- **Java Version**: Java 21
- **Security**: Spring Security + JWT
- **Database**: MySQL with JPA/Hibernate
- **Validation**: Bean Validation (JSR-303)
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

### Project Structure
```
src/main/java/com/jobtracking/
├── auth/                   # Authentication & User Management
│   ├── controller/         # Auth endpoints
│   ├── dto/               # Request/Response DTOs
│   ├── entity/            # User entity
│   ├── repository/        # User repository
│   └── service/           # Auth business logic
├── profile/               # User Profile Management
│   ├── controller/        # Profile endpoints
│   ├── dto/              # Profile DTOs
│   ├── entity/           # Profile entities (JobSeeker, Recruiter)
│   ├── repository/       # Profile repositories
│   └── service/          # Profile business logic
├── job/                  # Job Management
│   ├── controller/       # Job CRUD operations
│   ├── dto/             # Job DTOs
│   ├── entity/          # Job entity with relationships
│   ├── mapper/          # Job mapping utilities
│   ├── repository/      # Job repository with custom queries
│   └── service/         # Job business logic
├── application/          # Job Application Management
│   ├── controller/      # Application endpoints
│   ├── dto/            # Application DTOs
│   ├── entity/         # Application entity
│   ├── enums/          # Application status enum
│   ├── repository/     # Application repository
│   └── service/        # Application business logic
├── organization/         # Company/Organization Management
│   ├── controller/      # Organization endpoints
│   ├── dto/            # Organization DTOs
│   ├── entity/         # Organization entity
│   ├── repository/     # Organization repository
│   └── service/        # Organization business logic
├── admin/               # Admin Management
│   ├── controller/      # Admin endpoints
│   ├── dto/            # Admin DTOs
│   └── service/        # Admin services (Users, Jobs, Companies, Stats)
├── dashboard/           # Dashboard Statistics
├── audit/               # Audit Logging
├── report/              # Reporting Services
├── common/              # Shared utilities
│   ├── controller/      # Base controller
│   ├── entity/         # Base entities (BaseEntity, SoftDeleteEntity)
│   ├── exception/      # Custom exceptions and global handler
│   ├── mapper/         # Base mapper utilities
│   ├── repository/     # Base repositories
│   ├── response/       # API response wrapper
│   ├── service/        # Common services (Verification, Repository)
│   └── utils/          # Utility classes (Authorization, Response, Validation)
└── config/              # Configuration classes
    ├── DataInitializer.java    # Test data initialization
    ├── JwtAuthenticationFilter.java
    ├── SecurityConfig.java
    ├── SwaggerConfig.java
    └── JacksonConfig.java
```

## 🔐 Authentication & Authorization

### Role-Based Access Control
- **Admin (roleId: 1)**: Full system access, user management, company verification
- **Recruiter (roleId: 2)**: Job and application management, company profile
- **Job Seeker (roleId: 3)**: Job search and application submission

### JWT Token Authentication
```http
Authorization: Bearer <jwt_token>
```

### Test Accounts (Created by DataInitializer)
```
Admin:
Email: admin@jobtracking.com
Password: admin123
Role: Admin

Recruiter (with company):
Email: chaitanya@gmail.com
Password: chaitanya@123
Role: Recruiter
Company: TechSoft Pvt Ltd

Job Seeker:
Email: jobseeker@gmail.com
Password: jobseeker123
Role: Job Seeker
```

## 🏢 Company Verification System

### Company Profile Requirements
- **Recruiters must create a company** before posting jobs
- **Companies require admin verification** before job posting is allowed
- **Automatic RecruiterProfile creation** when company is created
- **Enhanced error messages** guide users through the process

### Company Verification Flow
1. Recruiter registers and logs in
2. Recruiter creates company profile
3. System automatically creates RecruiterProfile linked to company
4. Admin verifies the company
5. Recruiter can now post jobs

### Error Messages
The system provides specific, actionable error messages:
- `"Recruiter profile not found. Please create a company first to set up your recruiter profile."`
- `"Your company 'CompanyName' is not yet verified. Please contact admin for company verification before posting jobs."`
- `"No company associated with your profile. Please create or join a company before posting jobs."`

## 📚 API Documentation

### Enhanced Error Handling
All endpoints now return consistent error responses with specific guidance:

```json
{
  "success": false,
  "message": "Your company 'Google' is not yet verified. Please contact admin for company verification before posting jobs.",
  "data": null
}
```

### Authentication Endpoints

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "fullname": "John Doe",
  "username": "johndoe123",
  "email": "john@example.com",
  "phone": "+1234567890",
  "password": "password123",
  "roleId": 3
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "roleId": 3,
      "role": "JOB_SEEKER",
      "displayName": "J*** D***",
      "maskedEmail": "j***@example.com"
    }
  }
}
```

### Organization Management

#### Create Company (Recruiter Only)
```http
POST /organizations
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "My Tech Company",
  "website": "https://mytechcompany.com",
  "city": "Bangalore",
  "contactEmail": "hr@mytechcompany.com",
  "description": "Innovative technology solutions company"
}
```

#### Get My Company
```http
GET /organizations/my
Authorization: Bearer <token>
```

#### Check Company Exists
```http
GET /organizations/exists
Authorization: Bearer <token>
```

### Job Management (Enhanced)

#### Create Job (Requires Verified Company)
```http
POST /jobs?skillIds=1&skillIds=2&skillIds=3
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Senior Java Developer",
  "description": "Looking for experienced Java developer with Spring Boot expertise",
  "minSalary": 80000,
  "maxSalary": 120000,
  "location": "Bangalore",
  "jobType": "Full-time",
  "isActive": true
}
```

**Enhanced Validation:**
- Checks for RecruiterProfile existence
- Validates company association
- Verifies company verification status
- Provides specific error messages for each failure scenario

### Profile Management (Enhanced)

#### Get Recruiter Profile
```http
GET /profile/recruiter
Authorization: Bearer <token>
```

**Response for new recruiter (no company):**
```json
{
  "fullName": "Jane Smith",
  "email": "jane@example.com",
  "username": "janesmith",
  "bio": "Profile not yet created. Please create a company to set up your complete recruiter profile.",
  "phone": "+1234567890",
  "linkedinUrl": null,
  "yearsExperience": null,
  "specialization": null
}
```

#### Update Recruiter Profile
```http
PUT /profile/recruiter
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Jane Smith",
  "bio": "Senior recruiter with 5+ years experience",
  "phone": "+1234567890",
  "linkedinUrl": "https://linkedin.com/in/janesmith",
  "yearsExperience": 5,
  "specialization": "Software Engineering"
}
```

### Admin Endpoints (Enhanced)

#### Verify Company
```http
PUT /admin/companies/{companyId}/verify
Authorization: Bearer <admin_token>
```

#### Get Admin Statistics
```http
GET /admin/stats
Authorization: Bearer <admin_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "totalUsers": 150,
    "totalJobs": 45,
    "totalApplications": 320,
    "totalCompanies": 25,
    "verifiedCompanies": 18,
    "pendingApplications": 45
  }
}
```

## 🗄️ Database Schema (Updated)

### Key Relationships

#### RecruiterProfile → Organization (Enhanced)
```sql
CREATE TABLE recruiter_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    company_id BIGINT NOT NULL,  -- Required for job posting
    bio_en TEXT,
    phone VARCHAR(20),
    linkedin_url VARCHAR(500),
    years_experience INT,
    specialization VARCHAR(255),
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (company_id) REFERENCES companies(id)
);
```

#### Job → RecruiterProfile & Organization
```sql
CREATE TABLE jobs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    min_salary DECIMAL(10,2),
    max_salary DECIMAL(10,2),
    location VARCHAR(255),
    job_type VARCHAR(50),
    company_id BIGINT NOT NULL,
    recruiter_id BIGINT NOT NULL,  -- Links to recruiter_profile.id
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,  -- Soft delete support
    FOREIGN KEY (company_id) REFERENCES companies(id),
    FOREIGN KEY (recruiter_id) REFERENCES recruiter_profile(id)
);
```

#### Companies (Enhanced)
```sql
CREATE TABLE companies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    website VARCHAR(500),
    city VARCHAR(100),
    contact_email VARCHAR(255),
    description TEXT,
    verified BOOLEAN DEFAULT FALSE,  -- Admin verification required
    recruiter_user_id BIGINT NOT NULL,  -- Owner of the company
    extension JSON,  -- Additional metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (recruiter_user_id) REFERENCES users(id)
);
```

## 🔧 Configuration (Updated)

### Application Properties
```properties
# Server Configuration
server.port=5000
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/job_tracking2
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.hbm2ddl.halt_on_error=false

# JWT Configuration
jwt.secret=your-secret-key
jwt.expiration=86400000

# Logging Configuration
logging.level.com.jobtracking=DEBUG
logging.file.name=logs/job-tracking-backend.log
```

### Security Configuration (Enhanced)
```java
// Enhanced security with proper role mapping
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/jobs", "/jobs/**").permitAll()
                .requestMatchers("/organizations/**").hasRole("RECRUITER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

## 🚀 Data Initialization

### Automatic Test Data Creation
The `DataInitializer` creates comprehensive test data:

```java
@Component
@Profile({"default"})
public class DataInitializer implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        createTestUsers();      // Creates admin, recruiters, job seekers
        createTestData();       // Creates companies with linked profiles
        createTestApplications(); // Creates sample applications
    }
}
```

### What Gets Created:
1. **Users**: Admin, recruiters, job seekers
2. **Companies**: TechSoft Pvt Ltd, DataWorks Inc
3. **RecruiterProfiles**: Automatically linked to companies
4. **Jobs**: Sample jobs for each company
5. **Skills**: Comprehensive skill database
6. **Applications**: Sample job applications

## 🧪 Testing & Validation

### Enhanced Error Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(ValidationException ex) {
        return ResponseUtil.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorization(AuthorizationException ex) {
        return ResponseUtil.forbidden(ex.getMessage());
    }
}
```

### API Testing Commands
```bash
# Test job creation with proper error handling
curl -X POST "http://localhost:5000/api/jobs" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Job",
    "description": "Test description",
    "minSalary": 50000,
    "maxSalary": 70000,
    "location": "Test City",
    "jobType": "Full-time",
    "isActive": true
  }'

# Expected responses:
# - 400: "Recruiter profile not found. Please create a company first..."
# - 400: "Your company 'CompanyName' is not yet verified..."
# - 201: Job created successfully (if all requirements met)
```

## 🔒 Security Features (Enhanced)

### Authorization Utilities
```java
@Component
public class AuthorizationUtil {
    
    public boolean isRecruiterAuthorizedForOrganization(Long recruiterId, Long organizationId) {
        return verificationService.isRecruiterAuthorizedForOrganization(recruiterId, organizationId);
    }
    
    public boolean isRecruiterAuthorizedForJob(Long recruiterId, Long jobId) {
        // Checks both ownership and company verification
    }
}
```

### Verification Service
```java
@Service
public class VerificationService {
    
    public boolean isOrganizationVerified(Long organizationId) {
        return organizationRepository.findById(organizationId)
                .map(org -> org.getVerified() != null && org.getVerified())
                .orElse(false);
    }
}
```

## 🐛 Troubleshooting (Updated)

### Common Issues & Solutions

1. **Job Creation Fails with "Please create a company profile first"**
   ```sql
   -- Check if user has RecruiterProfile
   SELECT * FROM recruiter_profile WHERE user_id = YOUR_USER_ID;
   
   -- If missing, create it:
   INSERT INTO recruiter_profile (user_id, company_id, verified, created_at, updated_at)
   VALUES (YOUR_USER_ID, YOUR_COMPANY_ID, false, NOW(), NOW());
   ```

2. **Company Not Verified Error**
   ```sql
   -- Verify the company (admin action)
   UPDATE companies SET verified = true WHERE id = YOUR_COMPANY_ID;
   ```

3. **DataInitializer Not Running**
   - Check active profile is "default"
   - Verify database connection
   - Check application logs for initialization errors

4. **JWT Token Issues**
   - Token expires after 24 hours
   - Check Authorization header format: `Bearer <token>`
   - Verify JWT secret configuration

### Database Debugging
```sql
-- Check complete user setup
SELECT 
    u.id as user_id,
    u.email,
    u.role_id,
    c.id as company_id,
    c.name as company_name,
    c.verified as company_verified,
    rp.id as profile_id
FROM users u
LEFT JOIN companies c ON c.recruiter_user_id = u.id
LEFT JOIN recruiter_profile rp ON rp.user_id = u.id
WHERE u.email = 'your-email@example.com';
```

## 📊 Monitoring & Logging (Enhanced)

### Audit Logging
```java
@Service
public class AuditLogService {
    
    public void log(String entityType, Long entityId, String action, Long userId) {
        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setUserId(userId);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
```

### Application Logs
- User registration/login events
- Company creation and verification
- Job posting attempts and failures
- Profile updates
- Application submissions

### Health Monitoring
```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create feature branch from `develop`
3. Make changes with proper error handling
4. Add/update tests
5. Submit pull request to `develop`

### Code Standards
- Follow Spring Boot best practices
- Use proper exception handling
- Add comprehensive error messages
- Include audit logging for critical operations
- Write meaningful commit messages

---

**Version**: 2.0.0  
**Last Updated**: February 2025  
**Java Version**: 21  
**Spring Boot**: 3.2.1  
**Database**: MySQL 8.0  
**Maintainer**: Job Tracking Development Team