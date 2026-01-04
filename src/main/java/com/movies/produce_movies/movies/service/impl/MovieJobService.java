package com.movies.produce_movies.movies.service.impl;

import com.movies.produce_movies.entity.Movie;
import com.movies.produce_movies.movies.client.OmdbClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieJobService {

    private final OmdbClient omdbClient;

    /**
     * Calls OMDb search API from year 2015 to 2025 (10 times)
     */
    public List<Movie> fetchMoviesByYearRange() {
        List<Movie> movies = new ArrayList<>();
        int page = 1; // you can change this if pagination is needed
        for (int year = 2015; year <= 2025; year++) {
            log.info("Fetching movies for year: {}", year);
            movies.addAll(omdbClient.searchMovies("movie", year, page));
        }
        return movies;
    }
}
