package com.movies.produce_movies.repository;

import com.movies.produce_movies.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
}


