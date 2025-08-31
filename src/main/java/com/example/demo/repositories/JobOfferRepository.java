package com.example.demo.repositories;

import com.example.demo.models.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for JobOffer entities.
 * Extends JpaRepository to provide CRUD operations for job offers.
 */
@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
}