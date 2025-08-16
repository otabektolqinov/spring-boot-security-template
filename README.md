# Spring Security Authentication (JWT Branch)

This branch contains an implementation of authentication and authorization using **Spring Security with JWT (JSON Web Token)**.  
It demonstrates how to secure a Spring Boot application with stateless authentication, where access tokens and refresh tokens are used to manage user sessions.

---

## ✨ Features

- User authentication with **username & password**
- Stateless authentication using **JWT**
- **Access Token** and **Refresh Token** support
- Token validation (expiration, signature, subject)
- Role-based authorization
- Secure endpoints using Spring Security filters
- Custom exception handling for authentication/authorization errors

---

## 🚀 Tech Stack

- **Java 21+**
- **Spring Boot 3+**
- **Spring Security 6**
- **JJWT** (io.jsonwebtoken)
- **PostgreSQL** (or any relational DB)
- **Lombok**

---

## 🔑 Endpoints

### Authentication
- `POST /users/login` → authenticate user and return access + refresh tokens
- `POST /users/refresh` → generate new access token using refresh token

### Example Request (Login)
```json
POST /api/auth/login
{
  "username": "user1",
  "password": "password123"
}
