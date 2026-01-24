# Job Tracking Backend Service

A comprehensive Spring Boot REST API for job tracking application with role-based authentication, job management, and application tracking.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
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
   spring.datasource.url=jdbc:mysql://localhost:3306/jobtracking
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**
   - Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security + JWT
- **Database**: MySQL with JPA/Hibernate
- **Validation**: Bean Validation (JSR-303)
- **Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven

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
│   ├── entity/           # Profile entities
│   ├── repository/       # Profile repositories
│   └── service/          # Profile business logic
├── job/                  # Job Management
├── application/          # Job Application Management
├── organization/         # Company/Organization Management
├── admin/               # Admin Management
├── dashboard/           # Dashboard Statistics
├── audit/               # Audit Logging
├── common/              # Shared utilities
└── config/              # Configuration classes
```

## 🔐 Authentication & Authorization

### Role-Based Access Control
- **Admin (roleId: 1)**: Full system access
- **Recruiter (roleId: 2)**: Job and application management
- **Job Seeker (roleId: 3)**: Job search and application submission

### JWT Token Authentication
```http
Authorization: Bearer <jwt_token>
```

### Default Admin Account
```
Email: admin@jobtracking.com
Password: admin123
Role: Admin
```

## 📚 API Documentation

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

**Response:**
```http
200 OK
"User registered successfully"
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
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "roleId": 3,
  "fullname": "John Doe",
  "email": "john@example.com"
}
```

### Profile Endpoints

#### Get Job Seeker Profile
```http
GET /profile/jobseeker
Authorization: Bearer <token>
```

**Response:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "userName": "johndoe123",
  "phone": "+1234567890",
  "skills": ["Java", "Spring Boot", "React"],
  "resume": "https://example.com/resume",
  "about": "Experienced developer...",
  "education": {
    "degree": "Bachelor of Computer Science",
    "college": "Tech University",
    "year": 2020
  }
}
```

#### Update Job Seeker Profile
```http
PUT /profile/jobseeker
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "John Doe",
  "phone": "+1234567890",
  "skills": ["Java", "Spring Boot", "React"],
  "resume": "https://example.com/resume",
  "about": "Experienced developer...",
  "education": "{\"degree\":\"Bachelor of Computer Science\",\"college\":\"Tech University\",\"year\":2020}"
}
```

#### Get Recruiter Profile
```http
GET /profile/recruiter
Authorization: Bearer <token>
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

### Job Management Endpoints

#### Get All Jobs
```http
GET /jobs?page=0&size=10&sortBy=createdAt&sortDir=desc
Authorization: Bearer <token>
```

#### Get Job Details
```http
GET /jobs/{jobId}
Authorization: Bearer <token>
```

#### Create Job (Recruiter Only)
```http
POST /jobs
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Senior Java Developer",
  "description": "We are looking for...",
  "minSalary": 80000,
  "maxSalary": 120000,
  "location": "New York",
  "jobType": "Full-time",
  "companyId": 1,
  "skills": ["Java", "Spring Boot", "MySQL"]
}
```

### Application Management Endpoints

#### Apply for Job
```http
POST /applications/apply
Authorization: Bearer <token>
Content-Type: application/json

{
  "jobId": 1,
  "resumePath": "https://example.com/resume",
  "coverLetter": "I am interested in this position..."
}
```

#### Get My Applications
```http
GET /applications/my-applications?page=0&size=10
Authorization: Bearer <token>
```

#### Update Application Status (Recruiter Only)
```http
PUT /applications/{applicationId}/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "SHORTLISTED"
}
```

### Admin Endpoints

#### Get Admin Statistics
```http
GET /admin/stats
Authorization: Bearer <token>
```

#### Get All Users
```http
GET /admin/users?page=0&size=10
Authorization: Bearer <token>
```

#### Get All Applications
```http
GET /admin/applications?page=0&size=10
Authorization: Bearer <token>
```

## 🗄️ Database Schema

### Key Entities

#### User
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    fullname VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Job Seeker Profile
```sql
CREATE TABLE jobseeker_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    bio_en TEXT,
    bio_mr TEXT,
    education TEXT, -- JSON format
    experience TEXT,
    resume_link VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### Job
```sql
CREATE TABLE job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    min_salary DECIMAL(10,2),
    max_salary DECIMAL(10,2),
    location VARCHAR(255),
    job_type VARCHAR(50),
    company_id BIGINT,
    recruiter_user_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recruiter_user_id) REFERENCES users(id)
);
```

#### Application
```sql
CREATE TABLE application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'APPLIED',
    resume_path VARCHAR(500),
    cover_letter TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (job_id) REFERENCES job(id)
);
```

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/jobtracking
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=your-secret-key
jwt.expiration=86400000

# Server Configuration
server.port=8080

# Profile Configuration
spring.profiles.active=dev
```

### Security Configuration
- JWT-based authentication
- Role-based authorization
- CORS enabled for frontend integration
- Password encryption using BCrypt

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
- Unit tests for services
- Integration tests for controllers
- Repository tests with @DataJpaTest

## 🚀 Deployment

### Development
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production
```bash
mvn clean package
java -jar target/jobtracking-backend-1.0.0.jar --spring.profiles.active=prod
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/jobtracking-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📊 Monitoring & Logging

### Audit Logging
All critical operations are logged:
- User registration/login
- Profile updates
- Job creation/updates
- Application submissions

### Health Check
```http
GET /actuator/health
```

## 🔒 Security Features

- JWT token-based authentication
- Role-based access control (RBAC)
- Password encryption
- Input validation and sanitization
- SQL injection prevention
- CORS configuration
- Rate limiting headers

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Check MySQL service is running
   - Verify database credentials
   - Ensure database exists

2. **JWT Token Issues**
   - Check token expiration
   - Verify JWT secret configuration
   - Ensure proper Authorization header format

3. **Role Access Denied**
   - Verify user role in database
   - Check endpoint role requirements
   - Ensure proper role mapping

## 📝 API Response Formats

### Success Response
```json
{
  "data": { ... },
  "message": "Operation successful",
  "status": "success"
}
```

### Error Response
```json
{
  "error": "Error message",
  "status": "error",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Validation Error Response
```json
{
  "errors": {
    "email": "Please provide a valid email address",
    "password": "Password must be at least 6 characters"
  },
  "status": "validation_error"
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Contact the development team
- Check the documentation

---

**Version**: 1.0.0  
**Last Updated**: January 2024  
**Maintainer**: Job Tracking Development Team