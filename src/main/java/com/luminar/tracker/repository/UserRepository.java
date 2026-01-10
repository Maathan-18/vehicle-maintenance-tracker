package com.luminar.tracker.repository;

import com.luminar.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository
 * Spring Data JPA repository for User entity.
 * 
 * Demonstrates:
 * - Spring Data JPA automatic query method generation
 * - Custom query methods using method naming convention
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * Spring Data JPA automatically generates query from method name
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Find user by email and password (for login)
     * Note: In production, use Spring Security instead
     */
    Optional<User> findByEmailAndPassword(String email, String password);
}
