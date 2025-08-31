package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the JWT Security Demo.
 * 
 * This application demonstrates:
 * - JWT-based authentication and authorization
 * - Role-based access control (USER and ADMIN roles)
 * - RESTful API endpoints for job offer management
 * - Spring Security configuration with custom JWT filter
 * - User registration and login functionality
 */
@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
