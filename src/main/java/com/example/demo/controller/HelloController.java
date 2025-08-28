package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/public")
    public ResponseEntity<String> getPublic() {
        return ResponseEntity.ok("Hello getPublic");
    }

    @GetMapping("/private")
    public ResponseEntity<String> getPrivate() {
        return ResponseEntity.ok("Hello getPrivate");
    }

    @GetMapping("/private-admin")
    public ResponseEntity<String> getPrivateAdmin() {
        return ResponseEntity.ok("Hello Admin - private admin endpoint");
    }
}
