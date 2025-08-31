package com.example.demo.repositories;

import com.example.demo.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserApp entities.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Integer> {
    
    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserApp> findByUsername(String username);
}
