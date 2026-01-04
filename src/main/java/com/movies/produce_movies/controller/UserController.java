package com.movies.produce_movies.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies.produce_movies.entity.ProducedMovie;
import com.movies.produce_movies.entity.User;
import com.movies.produce_movies.movies.dto.UserHoldingsDTO;
import com.movies.produce_movies.movies.service.MovieService;
import com.movies.produce_movies.movies.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MovieService movieService;

    public UserController(UserService userService,
                          MovieService movieService) {
        this.userService = userService;
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody User user) {

        User created = userService.createUser(user);
        return ResponseEntity
            .created(URI.create("/api/users/" + created.getId()))
            .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @PathVariable Long id) {

        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user) {

        return ResponseEntity.ok(
            userService.updateUser(id, user)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    
    // User holdings for a movie
    @GetMapping("/{userId}/holdings")
    public ResponseEntity<List<ProducedMovie>> getUserHoldings(
            @PathVariable Long userId) {

        return ResponseEntity.ok(movieService.getUserInvestmentForMovie(userId));
    }
}
