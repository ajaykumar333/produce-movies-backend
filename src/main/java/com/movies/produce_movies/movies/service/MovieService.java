package com.movies.produce_movies.movies.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.movies.produce_movies.entity.Movie;
import com.movies.produce_movies.entity.ProducedMovie;
import com.movies.produce_movies.movies.dto.FavouriteMovieResponse;
import com.movies.produce_movies.movies.dto.FullMovieDetailsDTO;
import com.movies.produce_movies.movies.dto.MovieSummaryDto;
import com.movies.produce_movies.movies.dto.TradeMovieRequest;
import com.movies.produce_movies.movies.dto.UserHoldingsDTO;
import com.movies.produce_movies.movies.dto.UserMovieInvestmentDTO;

public interface MovieService {

    List<Movie> saveAllMoviesByYear(Integer year, int page);
    public Page<MovieSummaryDto> getAllMovies(
        String search,
        int page,
        int size,
        String sortBy,
        String direction
    );
    
    Map<String, Object> createMovie(FullMovieDetailsDTO movie);
    
    Map<String, Object> updateMovie(Long id, FullMovieDetailsDTO dto);

    FullMovieDetailsDTO getMovieDetails(Long id);
    
    Optional<Movie> getMovieByImdbId(String imdbId);

    List<FullMovieDetailsDTO> getAllMovies();
    
    void deleteMovie(Long id);
        
    void buyShares(TradeMovieRequest request);

    void sellShares(TradeMovieRequest request);
    
    UserHoldingsDTO getUserInvestmentForMovie(
    	    Long userId,
    	    Long movieId
    	);
	
	List<FavouriteMovieResponse> getAllFavouriteMovies(Long userId);
	
	FavouriteMovieResponse getFavouriteMovie(Long userId, Long movieId);
    
    void saveFavouriteMovie(Long userId, Long movieId, Boolean isLiked);
    
	List<ProducedMovie> getUserInvestmentForMovie(Long userId);
	    
}


