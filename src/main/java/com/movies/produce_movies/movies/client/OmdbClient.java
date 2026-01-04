package com.movies.produce_movies.movies.client;

import com.movies.produce_movies.config.OmdbProperties;
import com.movies.produce_movies.entity.Movie;
import com.movies.produce_movies.repository.MovieRepository;

import tools.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class OmdbClient {

    private static final Logger log = LoggerFactory.getLogger(OmdbClient.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OmdbProperties properties;
    @Autowired
    private MovieRepository movieRepository;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetch movies from OMDb, save full data, and return saved movies
     */
    public List<Movie> searchMovies(String query, Integer year, int page) {

        List<Movie> savedMovies = new ArrayList<>();
        URI searchUri = buildSearchUri(query, year, page);

        try {
            ResponseEntity<Map> response =
                    restTemplate.getForEntity(searchUri, Map.class);

            Map<String, Object> body = response.getBody();

            if (body == null || "False".equals(body.get("Response"))) {
                return savedMovies;
            }

            Object search = body.getOrDefault("Search", Collections.emptyList());
            List<Map<String, Object>> results =
                    (search instanceof List)
                            ? (List<Map<String, Object>>) search
                            : Collections.emptyList();

            for (Map<String, Object> summary : results) {

                String imdbId = (String) summary.get("imdbID");

                // Return existing movie if already saved
                Movie existing = movieRepository.findByImdbId(imdbId).orElse(null);
                if (existing != null) {
                    savedMovies.add(existing);
                    continue;
                }

                Map<String, Object> details = fetchMovieDetails(imdbId);
                Movie movie = mapToEntity(details);

                Movie saved = movieRepository.save(movie);
                savedMovies.add(saved);
            }

        } catch (RestClientException e) {
            log.error("Error calling OMDb API", e);
        }

        return savedMovies;
    }

    private Map<String, Object> fetchMovieDetails(String imdbId) {

        URI uri = UriComponentsBuilder
                .fromUriString(properties.getBaseUrl())
                .queryParam("apikey", properties.getApiKey())
                .queryParam("i", imdbId)
                .queryParam("plot", "full")
                .build()
                .toUri();

        ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
        return response.getBody();
    }

    private Movie mapToEntity(Map<String, Object> raw) {

        Movie movie = new Movie();

        movie.setImdbId((String) raw.get("imdbID"));
        movie.setTitle((String) raw.getOrDefault("Title", ""));
        movie.setYear((String) raw.getOrDefault("Year", ""));
        movie.setType((String) raw.getOrDefault("Type", ""));
        movie.setRated((String) raw.get("Rated"));
        movie.setReleased((String) raw.get("Released"));
        movie.setRuntime((String) raw.get("Runtime"));
        movie.setGenre((String) raw.get("Genre"));
        movie.setDirector((String) raw.get("Director"));
        movie.setWriter((String) raw.get("Writer"));
        movie.setActors((String) raw.get("Actors"));
        movie.setPlot((String) raw.get("Plot"));
        movie.setLanguage((String) raw.get("Language"));
        movie.setCountry((String) raw.get("Country"));
        movie.setAwards((String) raw.get("Awards"));
        movie.setMetascore((String) raw.get("Metascore"));
        movie.setImdbRating((String) raw.get("imdbRating"));
        movie.setImdbVotes((String) raw.get("imdbVotes"));
        movie.setDvd((String) raw.get("DVD"));
        movie.setBoxOffice((String) raw.get("BoxOffice"));
        movie.setProduction((String) raw.get("Production"));
        movie.setWebsite((String) raw.get("Website"));
        movie.setPoster((String) raw.get("Poster"));

        try {
            Object ratings = raw.get("Ratings");
            movie.setRatingsJson(
                    ratings != null ? objectMapper.writeValueAsString(ratings) : null
            );
        } catch (Exception e) {
            log.warn("Failed to serialize ratings for {}", movie.getImdbId());
        }

        return movie;
    }

    private URI buildSearchUri(String query, Integer year, int page) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(properties.getBaseUrl())
                .queryParam("apikey", properties.getApiKey())
                .queryParam("s", query)
                .queryParam("type", "movie")
                .queryParam("page", page);

        if (year != null) {
            builder.queryParam("y", year);
        }

        return builder.build().toUri();
    }
}
