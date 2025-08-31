package com.example.demo;

import com.example.demo.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class that defines the security filter chain
 * and authentication/authorization rules for the application.
 * Configures JWT-based authentication with role-based access control.
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtService jwtService;

    /**
     * Provides a BCrypt password encoder bean for secure password hashing.
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain with JWT authentication and authorization rules.
     * Security configuration:
     * - Disables CSRF protection (stateless JWT authentication)
     * - Allows same-origin frame options for H2 console
     * - Public endpoints: /, /login, /register, /hello/public, /auth/*, /h2-console/**, GET /offers
     * - Authenticated endpoints: POST/DELETE /offers, /add-offer, /hello/private
     * - Admin-only endpoints: /hello/private-admin
     * - Adds JWT filter before username/password authentication filter
     * 
     * @param http the HttpSecurity configuration object
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register").permitAll()
                        .requestMatchers("/hello/public").permitAll()
                        .requestMatchers("/auth/login", "/auth/register", "/auth/status", "/auth/logout").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/offers").permitAll()
                .requestMatchers(HttpMethod.POST, "/offers").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/offers/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/add-offer").authenticated()
                        .requestMatchers("/hello/private-admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtService, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
