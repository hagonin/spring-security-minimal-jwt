package com.example.demo.controller;

import com.example.demo.models.JobOffer;
import com.example.demo.models.Role;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/offers")
public class JobOfferController {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @GetMapping
    public ResponseEntity<List<JobOffer>> getAllOffers() {
        return ResponseEntity.ok(jobOfferRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<JobOffer> createOffer(@RequestBody JobOffer jobOffer, @AuthenticationPrincipal UserApp currentUser) {
        jobOffer.setOwner(currentUser);
        JobOffer savedOffer = jobOfferRepository.save(jobOffer);
        return ResponseEntity.ok(savedOffer);
    }

    @DeleteMapping("/{id}")
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