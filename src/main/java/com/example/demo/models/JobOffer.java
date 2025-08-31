package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a job offer in the application.
 * Stores job details and associates each offer with a user owner.
 * Uses Lombok annotations for automatic getter/setter generation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_offer")
public class JobOffer {

    /** Primary key for the job offer entity */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Job title */
    private String title;
    
    /** Detailed job description */
    private String description;
    
    /** Company offering the job */
    private String company;
    
    /** Salary offered for the position */
    private Double salary;
    
    /** User who created this job offer */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private UserApp owner;

    /**
     * Constructor for creating a job offer with all details.
     * @param title the job title
     * @param description the job description
     * @param company the company name
     * @param salary the offered salary
     * @param owner the user who created the job offer
     */
    public JobOffer(String title, String description, String company, Double salary, UserApp owner) {
        this.title = title;
        this.description = description;
        this.company = company;
        this.salary = salary;
        this.owner = owner;
    }
}