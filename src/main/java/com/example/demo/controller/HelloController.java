package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling hello world endpoints with different security levels.
 * Provides public and private endpoints for testing authentication and authorization.
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    /**
     * Public endpoint accessible without authentication.
     * @return ResponseEntity with a greeting message
     */
    @GetMapping("/public")
    public ResponseEntity<String> getPublic() {
        return ResponseEntity.ok("Hello getPublic");
    }

    /**
     * Private endpoint that requires authentication.
     * @return ResponseEntity with a private greeting message
     */
    @GetMapping("/private")
    public ResponseEntity<String> getPrivate() {
        return ResponseEntity.ok("Hello getPrivate");
    }

    /**
     * Admin-only private endpoint that requires ADMIN role.
     * @return ResponseEntity with an admin greeting message
     */
    @GetMapping("/private-admin")
    public ResponseEntity<String> getPrivateAdmin() {
        return ResponseEntity.ok("Hello Admin - private admin endpoint");
    }
}
