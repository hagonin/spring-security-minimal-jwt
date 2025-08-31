package com.example.demo.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user in the application.
 * Stores user credentials and role information with JPA persistence.
 * Uses Lombok annotations for automatic getter/setter generation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_app")
public class UserApp {

    /** Primary key for the user entity */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** Unique username for authentication */
    @Column(unique = true)
    private String username;
    
    /** Encrypted password for authentication */
    private String password;
    
    /** User role for authorization (USER or ADMIN) */
    @Enumerated(EnumType.STRING)
    private Role role;


    /**
     * Constructor for creating a user with specified username, password, and role.
     * @param username the user's username
     * @param password the user's encrypted password
     * @param role the user's role (USER or ADMIN)
     */
    public UserApp(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    /**
     * Constructor for creating a user with username and password.
     * Defaults role to USER.
     * 
     * @param username the user's username
     * @param password the user's encrypted password
     */
    public UserApp(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = Role.USER;
    }
}
