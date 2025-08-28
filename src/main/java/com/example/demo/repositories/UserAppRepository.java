package com.example.demo.repositories;

import com.example.demo.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Integer> {
    public Optional<UserApp> findByUsername(String username);
}
