package com.example.demo.controller;

import com.example.demo.models.Role;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import com.example.demo.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    JwtService jwtService;
    @Autowired
    UserAppRepository userAppRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Value("${jwt.cookie_name}")
    private String cookieName;

    @GetMapping("/")
    public String index() {
        return "redirect:/offers";
    }

    @GetMapping("/add-offer")
    public String addOfferPage() {
        return "add-offer";
    }

    @GetMapping("/login")
    public Object loginPage(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        
        // If request is from browser (HTML), return the template
        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            return new ModelAndView("login");
        }
        
        // Otherwise redirect to login page
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/register") 
    public Object registerPage(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        
        // If request is from browser (HTML), return the template
        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            return new ModelAndView("register");
        }
        
        // Otherwise redirect to register page
        return new ModelAndView("redirect:/register");
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody UserApp userApp) throws Exception {
        Optional<UserApp> userAppOptional = userAppRepository.findByUsername(userApp.getUsername());
        if (userAppOptional.isPresent() && passwordEncoder.matches(userApp.getPassword(), userAppOptional.get().getPassword())) {
            UserApp foundUser = userAppOptional.get();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtService.createAuthenticationToken(foundUser).toString()).body("connected");
        }
        throw new Exception();
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public void register(@RequestBody UserApp userApp) throws Exception {
        Optional<UserApp> userAppOptional = userAppRepository.findByUsername(userApp.getUsername());
        if (userAppOptional.isEmpty()) {
            Role role = userApp.getRole() != null ? userApp.getRole() : Role.USER;
            userAppRepository.save(
                    new UserApp(
                            userApp.getUsername(),
                            passwordEncoder.encode(userApp.getPassword()),
                            role
                    )
            );
        }else{
            throw new Exception();
        }
    }

    @PostMapping("/auth/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .body("Logged out successfully");
    }

    @GetMapping("/auth/status")
    @ResponseBody
    public ResponseEntity<?> getAuthStatus(@AuthenticationPrincipal UserApp currentUser) {
        if (currentUser != null) {
            return ResponseEntity.ok(currentUser);
        }
        return ResponseEntity.status(401).body("Not authenticated");
    }
}
