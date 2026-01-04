package com.movies.produce_movies.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movies.produce_movies.entity.ProducedMovie;

public interface ProducedMovieRepository
        extends JpaRepository<ProducedMovie, Long> {

    Optional<ProducedMovie> findByUserIdAndMovieId(
        Long userId, Long movieId
    );
    
    Optional<ProducedMovie> findByUserIdAndMovieIdAndIsProducedTrue(
            Long userId, Long movieId
        );

    List<ProducedMovie> findByUserId(Long userId);
    
    Optional<ProducedMovie> findByUserIdAndMovieIdAndIsLikedTrue(
            Long userId,
            Long movieId
    );
    
    List<ProducedMovie> findByUserIdAndIsLikedTrue(Long userId);
    
    List<ProducedMovie> findByUserIdAndIsProducedTrue(Long userId);


}
