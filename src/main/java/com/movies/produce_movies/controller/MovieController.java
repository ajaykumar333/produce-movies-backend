package com.movies.produce_movies.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movies.produce_movies.entity.ProducedMovie;
import com.movies.produce_movies.movies.dto.FavouriteMovieResponse;
import com.movies.produce_movies.movies.dto.FullMovieDetailsDTO;
import com.movies.produce_movies.movies.dto.MovieSummaryDto;
import com.movies.produce_movies.movies.dto.ProduceMovieRequest;
import com.movies.produce_movies.movies.dto.TradeMovieRequest;
import com.movies.produce_movies.movies.dto.UserHoldingsDTO;
import com.movies.produce_movies.movies.dto.UserMovieInvestmentDTO;
import com.movies.produce_movies.movies.service.MovieService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {

    @Autowired
    private MovieService movieService;

    // Paged movies
    @GetMapping("/paged-movies")
    public ResponseEntity<Page<MovieSummaryDto>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(
            movieService.getAllMovies(search, page, size, sortBy, direction)
        );
    }

    @GetMapping("/favourite-movies/all")
    public ResponseEntity<List<FavouriteMovieResponse>> getAllFavouriteMovies(
            @RequestParam Long userId) {
        return ResponseEntity.ok(
            movieService.getAllFavouriteMovies(userId)
        );
    }

    @GetMapping("/favourite-movies")
    public ResponseEntity<FavouriteMovieResponse> getFavouriteMovie(
    		@RequestParam Long userId, @RequestParam Long movieId) {

        return ResponseEntity.ok(
            movieService.getFavouriteMovie(userId, movieId)
        );
    }
    
    @PostMapping("/favourite")
    public ResponseEntity<Void> saveFavouriteMovie(@Valid @RequestBody ProduceMovieRequest request) {
        movieService.saveFavouriteMovie(
            request.getUserId(), 
            request.getMovieId(), 
            request.getIsLiked()
        );
        return ResponseEntity.ok().build();
    }
    
    // Buy shares
    @PostMapping("/produce")
    public ResponseEntity<Void> buyShares(
            @Valid @RequestBody TradeMovieRequest request) {
        movieService.buyShares(request);
        return ResponseEntity.ok().build();
    }

    // Sell shares
    @PostMapping("/sell")
    public ResponseEntity<Void> sellShares(
            @Valid @RequestBody TradeMovieRequest request) {
        movieService.sellShares(request);
        return ResponseEntity.ok().build();
    }

    // Movie details
    @GetMapping("/{id}")
    public ResponseEntity<FullMovieDetailsDTO> getMovie(
            @PathVariable Long id) {

        return ResponseEntity.ok(movieService.getMovieDetails(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createMovie(
            @RequestBody FullMovieDetailsDTO dto) {

        return ResponseEntity.ok(movieService.createMovie(dto));
    }
	
    // Update movie
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody FullMovieDetailsDTO movie) {

        return ResponseEntity.ok(
            movieService.updateMovie(id, movie)
        );
    }

    // Delete movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(
            @PathVariable Long id) {

        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
    
 // User holdings for a movie
    @GetMapping("/{movieId}/users/{userId}/investment")
    public ResponseEntity<UserHoldingsDTO> getUserHoldings(
            @PathVariable Long userId,
            @PathVariable Long movieId) {

        return ResponseEntity.ok(movieService.getUserInvestmentForMovie(userId, movieId));
    }
    
}
