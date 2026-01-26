# 🔐 Authentication & Authorization (Spring Security + JWT)

This project implements **stateless authentication and role-based authorization** using **Spring Security and JWT**, designed in a **microservice-ready architecture**.

---

## 📌 High-Level Flow

1. User **registers** using `/auth/register`
2. Password is **encrypted using BCrypt**
3. User **logs in** using `/auth/login`
4. Backend generates **JWT token**
5. Frontend sends token in `Authorization: Bearer <token>`
6. **JWT Filter** validates token for every request
7. **Role-based access** is enforced using Spring Security

---

## 🧱 Database Design (Auth Related)

### `roles` table

| id | name       |
| -- | ---------- |
| 1  | ADMIN      |
| 2  | RECRUITER  |
| 3  | JOB_SEEKER |

### `users` table (important fields)

| Column        | Description               |
| ------------- | ------------------------- |
| id            | Primary key               |
| username      | Unique username           |
| email         | Login identifier          |
| password_hash | BCrypt encrypted password |
| role_id       | FK → roles.id             |
| active        | Enable/disable user       |
| created_at    | Auto timestamp            |
| updated_at    | Auto timestamp            |

---

## 🔑 Password Encryption (Why BCrypt?)

We use **BCryptPasswordEncoder** because:

* One-way encryption (cannot be decrypted)
* Salt included automatically
* Industry standard
* Protects against rainbow table attacks

### Configuration

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## 📝 Register API

### Endpoint

```
POST /api/auth/register
```

### Request Payload

```json
{
  "username": "vivek",
  "email": "vivek@mail.com",
  "password": "123456",
  "roleId": 3,
  "fullname": "Vivek Bhosale"
}
```

### What happens internally

1. Check if email already exists
2. Encrypt password using BCrypt
3. Save user with roleId
4. DB foreign key validates role

### Key Code (Service Layer)

```java
User user = User.builder()
    .username(request.getUsername())
    .email(request.getEmail())
    .passwordHash(passwordEncoder.encode(request.getPassword()))
    .roleId(request.getRoleId())
    .fullname(request.getFullname())
    .active(true)
    .build();
```

---

## 🔓 Login API

### Endpoint

```
POST /api/auth/login
```

### Request

```json
{
  "email": "vivek@mail.com",
  "password": "123456"
}
```

### Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 3,
  "roleId": 3
}
```

### Login Logic

1. Fetch user by email
2. Verify password using `matches()`
3. Generate JWT token
4. Return token to client

### Key Code

```java
if (!passwordEncoder.matches(
        request.getPassword(),
        user.getPasswordHash())) {
    throw new RuntimeException("Invalid credentials");
}
```

---

## 🔐 JWT (JSON Web Token)

### Why JWT?

* Stateless authentication
* No server session
* Perfect for microservices
* Easily scalable

### Token Contains

* `sub` → userId
* `roleId` → authorization

### JWT Generation

```java
Jwts.builder()
    .subject(userId.toString())
    .claim("roleId", roleId)
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + expirationMs))
    .signWith(secretKey)
    .compact();
```

---

## 🧪 JWT Validation (Filter)

### `JwtAuthenticationFilter`

* Runs **once per request**
* Extracts token from header
* Validates token
* Sets authentication in `SecurityContext`

### Header Format

```
Authorization: Bearer <JWT_TOKEN>
```

### Key Logic

```java
Claims claims = jwtUtil.extractClaims(token);

UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(
        userId,
        null,
        authorities
    );

SecurityContextHolder.getContext()
    .setAuthentication(authentication);
```

---

## 🔒 Spring Security Configuration

### Stateless Security

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

### Public & Protected APIs

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**").permitAll()
    .requestMatchers("/admin/**").hasAuthority("ADMIN")
    .anyRequest().authenticated()
)
```

### Why `STATELESS`?

* No HTTP session
* Every request validated by JWT
* Required for microservices

---

## 🧠 Role-Based Authorization

### Role Mapping

```java
roleId → authority
1 → ADMIN
2 → RECRUITER
3 → JOB_SEEKER
```

### Utility Class

```java
public static String toAuthority(Integer roleId) {
    return switch (roleId) {
        case 1 -> "ADMIN";
        case 2 -> "RECRUITER";
        case 3 -> "JOB_SEEKER";
        default -> "UNKNOWN";
    };
}
```

---

## 🛡️ Security Highlights (Interview Points)

✔ Password never stored in plain text
✔ JWT used instead of sessions
✔ Stateless backend
✔ Role-based access control
✔ DB-first design with FK constraints
✔ Microservice-ready architecture

---

## ❓ Common Interview Questions (With Answers)

### Q1: Why JWT over session?

> JWT is stateless, scalable, and ideal for microservices.

### Q2: Why BCrypt?

> It is salted, slow by design, and secure against brute-force attacks.

### Q3: Where is JWT validated?

> In a custom `OncePerRequestFilter`.

### Q4: How role-based access is enforced?

> Using Spring Security authorities extracted from JWT.

### Q5: Can this scale to microservices?

> Yes, JWT allows independent services without shared session state.
