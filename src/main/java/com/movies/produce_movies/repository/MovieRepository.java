package com.movies.produce_movies.repository;

import com.movies.produce_movies.entity.Movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByImdbId(String imdbId);
    Page<Movie> findByTitleContainingIgnoreCaseAndIsActiveTrue(
            String title,
            Pageable pageable
    );
    Page<Movie> findByIsActiveTrue(Pageable pageable);
    
    Optional<Movie> findByIdAndIsActiveTrue(Long userid);

}
