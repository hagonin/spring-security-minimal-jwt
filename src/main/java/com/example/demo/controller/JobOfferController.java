package com.example.demo.controller;

import com.example.demo.models.JobOffer;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.JobOfferRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.Optional;

@Controller
@RequestMapping("/offers")
public class JobOfferController {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    /**
     * Retrieves all job offers. Returns HTML view for browser requests
     * and JSON for API calls.
     * @param request the HTTP servlet request
     * @return ModelAndView for HTML requests or ResponseEntity with job offers for JSON requests
     */
    @GetMapping
    public Object getAllOffers(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        
        // If request is from browser (HTML), return the template
        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            return new ModelAndView("offers");
        }
        
        // Otherwise, return JSON for API calls
        return ResponseEntity.ok(jobOfferRepository.findAll());
    }

    /**
     * Creates a new job offer for the authenticated user.
     * @param jobOffer the job offer to create
     * @param currentUser the authenticated user
     * @return ResponseEntity with the created job offer
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<JobOffer> createOffer(@RequestBody JobOffer jobOffer, @AuthenticationPrincipal UserApp currentUser) {
        jobOffer.setOwner(currentUser);
        JobOffer savedOffer = jobOfferRepository.save(jobOffer);
        return ResponseEntity.ok(savedOffer);
    }

    /**
     * Deletes a job offer by ID. Users can only delete their own job offers
     * unless they have ADMIN role.
     * @param id the ID of the job offer to delete
     * @param currentUser the authenticated user
     * @return ResponseEntity with success message or error status
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteOffer(@PathVariable Long id, @AuthenticationPrincipal UserApp currentUser) {
        Optional<JobOffer> offerOptional = jobOfferRepository.findById(id);
        
        if (offerOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        JobOffer offer = offerOptional.get();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        if (!isAdmin && !offer.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("You can only delete your own job offers");
        }
        
        jobOfferRepository.delete(offer);
        return ResponseEntity.ok("Job offer deleted successfully");
    }
}