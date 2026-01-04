package com.movies.produce_movies;

import com.movies.produce_movies.entity.Movie;
import com.movies.produce_movies.movies.service.impl.MovieJobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store-in-db")
public class MovieJobController {

    @Autowired
    private MovieJobService movieJobService;

    /**
     * Triggers OMDb job for years 2015 to 2025
     */
    @GetMapping("/fetch-by-year-range")
    public ResponseEntity<List<Movie>> fetchMoviesByYearRange() {
        List<Movie> movies =
                movieJobService.fetchMoviesByYearRange();

        return ResponseEntity.ok(movies);
    }
}
