package com.movies.produce_movies.movies.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movies.produce_movies.entity.Movie;
import com.movies.produce_movies.entity.ProducedMovie;
import com.movies.produce_movies.entity.User;
import com.movies.produce_movies.exception.ResourceNotFoundException;
import com.movies.produce_movies.movies.client.OmdbClient;
import com.movies.produce_movies.movies.dto.FavouriteMovieResponse;
import com.movies.produce_movies.movies.dto.FullMovieDetailsDTO;
import com.movies.produce_movies.movies.dto.MovieSummaryDto;
import com.movies.produce_movies.movies.dto.TradeMovieRequest;
import com.movies.produce_movies.movies.dto.UserHoldingsDTO;
import com.movies.produce_movies.movies.dto.UserMovieInvestmentDTO;
import com.movies.produce_movies.movies.service.MovieService;
import com.movies.produce_movies.repository.MovieRepository;
import com.movies.produce_movies.repository.ProducedMovieRepository;
import com.movies.produce_movies.repository.UserRepository;

@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	private OmdbClient omdbClient;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProducedMovieRepository producedMovieRepository;

	@Override
	public Page<MovieSummaryDto> getAllMovies(String search, int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Movie> moviePage;

		if (search != null && !search.isBlank()) {
			moviePage = movieRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue(search, pageable);
		} else {
			moviePage = movieRepository.findByIsActiveTrue(pageable);
		}

		return moviePage.map(movie -> MovieSummaryDto.builder().id(movie.getId()).imdbId(movie.getImdbId())
				.title(movie.getTitle()).year(movie.getYear()).type(movie.getType()).poster(movie.getPoster())
				.actors(movie.getActors()).director(movie.getDirector()).sharePrice(movie.getSharePrice())
				.movieBudget(movie.getMovieBudget()).leftOverBudget(movie.getLeftOverBudget()).build());
	}

	@Override
	public List<Movie> saveAllMoviesByYear(Integer year, int page) {
		List<Movie> movies = omdbClient.searchMovies("movie", year, page);
		movieRepository.saveAll(movies);
		return movies;
	}

	@Override
	@Transactional
	public Map<String, Object> createMovie(FullMovieDetailsDTO dto) {

		Map<String, Object> response = new HashMap<>();

		try {
			Movie entity = new Movie();
			entity.setId(null);

			// DTO → Entity
			BeanUtils.copyProperties(dto, entity);

			// Initialize leftover budget
			if (entity.getMovieBudget() != null && entity.getLeftOverBudget() == null) {
				entity.setLeftOverBudget(entity.getMovieBudget());
			}

			Movie saved = movieRepository.save(entity);

			FullMovieDetailsDTO result = new FullMovieDetailsDTO();
			BeanUtils.copyProperties(saved, result);

			response.put("success", true);
			response.put("data", result);

		} catch (Exception e) {
			response.put("success", false);
			response.put("error", e.getMessage());
		}

		return response;
	}

	@Override
	@Transactional
	public Map<String, Object> updateMovie(Long id, FullMovieDetailsDTO dto) {

		Map<String, Object> response = new HashMap<>();

		try {
			Movie existingMovie = getMovie(id);

			// Copy safe fields only
			BeanUtils.copyProperties(dto, existingMovie, "id", "createdAt", "updatedAt");

			movieRepository.save(existingMovie);
			response.put("success", true);

		} catch (Exception e) {
			response.put("success", false);
			response.put("error", e.getMessage());
		}

		return response;
	}

	@Transactional(readOnly = true)
	public FullMovieDetailsDTO getMovieDetails(Long id) {

		Movie movie = getMovie(id);
		FullMovieDetailsDTO dto = new FullMovieDetailsDTO();
		BeanUtils.copyProperties(movie, dto);

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Movie> getMovieByImdbId(String imdbId) {
		return movieRepository.findByImdbId(imdbId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FullMovieDetailsDTO> getAllMovies() {

		List<Movie> dbMovies = movieRepository.findAll();

		return dbMovies.stream().map(movie -> {
			FullMovieDetailsDTO dto = new FullMovieDetailsDTO();
			BeanUtils.copyProperties(movie, dto);
			return dto;
		}).toList();
	}

	@Override
	public void deleteMovie(Long id) {
		Movie movie = getMovie(id);
		movie.setIsActive(false);
		movieRepository.save(movie);
	}
	
	@Transactional
	public void buyShares(TradeMovieRequest request) {

	    Movie movie = getMovie(request.getMovieId());
	    User user = getUser(request.getUserId());

	    ProducedMovie investment = getOrCreateProducedMovie(user, movie);

	    validateUserCanBuyShares(
	        investment,
	        movie,
	        request.getShares()
	    );

	    int sharesToBuy = request.getShares();
	    int sharesPerLot = movie.getSharesPerLot();

	    int totalSharesAfterBuy = investment.getShares() + sharesToBuy;
	    int totalLotsAfterBuy = totalSharesAfterBuy / sharesPerLot;

	    double amount = sharesToBuy * movie.getSharePrice();

	    if (movie.getLeftOverBudget() < amount) {
	        throw new IllegalStateException("Insufficient movie budget");
	    }

	    // Update investment
	    investment.setShares(totalSharesAfterBuy);
	    investment.setLots(totalLotsAfterBuy);
	    investment.setInvestedAmount(investment.getInvestedAmount() + amount);

	    // Update movie & user
	    movie.setLeftOverBudget(movie.getLeftOverBudget() - amount);
	    user.setPortFolioWorth(
	        (user.getPortFolioWorth() == null ? 0 : user.getPortFolioWorth()) + amount
	    );

	    producedMovieRepository.save(investment);
	}

	@Transactional
	public void sellShares(TradeMovieRequest request) {

		ProducedMovie investment = producedMovieRepository
				.findByUserIdAndMovieId(request.getUserId(), request.getMovieId())
				.orElseThrow(() -> new IllegalStateException("No investment found"));

		int sharesToSell = request.getShares();

		if (sharesToSell > investment.getShares()) {
			throw new IllegalStateException("Cannot sell more shares than owned");
		}

		Movie movie = investment.getMovie();
		User user = investment.getUser();

		double amount = sharesToSell * movie.getSharePrice();

		// ✅ Update investment
		investment.setShares(investment.getShares() - sharesToSell);
		investment.setLots(investment.getShares() / movie.getSharesPerLot());
		investment.setInvestedAmount(investment.getInvestedAmount() - amount);

		// ✅ Reverse budget & portfolio
		movie.setLeftOverBudget(movie.getLeftOverBudget() + amount);
		user.setPortFolioWorth(user.getPortFolioWorth() - amount);

		producedMovieRepository.save(investment);
		movieRepository.save(movie);
		userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserHoldingsDTO getUserInvestmentForMovie(Long userId, Long movieId) {

		 Optional<ProducedMovie> producedMovie = producedMovieRepository
			        .findByUserIdAndMovieId(userId, movieId);
		 UserHoldingsDTO holdingsDTO = new UserHoldingsDTO();
		 if(!producedMovie.isEmpty()) {
			 holdingsDTO.setInvestedAmount(producedMovie.get().getInvestedAmount());
			 holdingsDTO.setLots(producedMovie.get().getLots());
			 holdingsDTO.setShares(producedMovie.get().getShares());
		 }
		 return holdingsDTO;
	}

	protected User getUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
	}

	protected Movie getMovie(Long movieId) {
		return movieRepository.findByIdAndIsActiveTrue(movieId)
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
	}

	protected ProducedMovie getProducedMovie(Long movieId, Long userId) {
		return producedMovieRepository.findByUserIdAndMovieIdAndIsProducedTrue(userId, movieId)
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
	}

	protected FullMovieDetailsDTO entityToDto(Movie entity) {
		FullMovieDetailsDTO dto = new FullMovieDetailsDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	protected Movie dtoToEntity(FullMovieDetailsDTO dto) {
		Movie entity = new Movie();
		BeanUtils.copyProperties(dto, entity);
		return entity;
	}

	@Override
	public FavouriteMovieResponse getFavouriteMovie(Long userId, Long movieId) {

	    // Validate user exists (optional but good)
	    getUser(userId);

	    ProducedMovie pm = producedMovieRepository
	            .findByUserIdAndMovieIdAndIsLikedTrue(userId, movieId)
	            .orElseThrow(() ->
	                new IllegalStateException("Favourite movie not found")
	            );

	    Movie movie = pm.getMovie();

	    return FavouriteMovieResponse.builder()
	            .id(movie.getId())
	            .title(movie.getTitle())
	            .year(movie.getYear())
	            .type(movie.getType())
	            .poster(movie.getPoster())
	            .director(movie.getDirector())
	            .actors(movie.getActors())
	            .movieBudget(movie.getMovieBudget())
	            .leftOverBudget(movie.getLeftOverBudget())
	            .sharePrice(movie.getSharePrice())
	            .lotPrice(movie.getLotPrice())
	            .sharesPerLot(movie.getSharesPerLot())
	            .maxLots(movie.getMaxLots())
	            .maxUserLots(movie.getMaxUserLots())
	            .isProduced(pm.isProduced())
	            .isLiked(pm.isLiked())
	            .build();
	}

	@Override
	public void saveFavouriteMovie(Long userId, Long movieId, Boolean isLiked) {

		if (userId == null || movieId == null) {
			throw new IllegalArgumentException("UserId and MovieId must not be null");
		}

		User user = getUser(userId);
		Movie movie = getMovie(movieId);

		Optional<ProducedMovie> existing = producedMovieRepository.findByUserIdAndMovieId(userId, movieId);

		// CASE 1: Already exists
		if (existing.isPresent()) {
			ProducedMovie producedMovie = existing.get();
			producedMovie.setLiked(isLiked);
			producedMovieRepository.save(producedMovie);
		}else {
			// CASE 2: No record exists → create new
			ProducedMovie producedMovie = new ProducedMovie();
			producedMovie.setUser(user);
			producedMovie.setMovie(movie);
			producedMovie.setLiked(true);
			producedMovieRepository.save(producedMovie);	
		}
	}

	@Override
	public List<FavouriteMovieResponse> getAllFavouriteMovies(Long userId) {

	    return producedMovieRepository.findByUserIdAndIsLikedTrue(userId)
	        .stream()
	        .map(pm -> FavouriteMovieResponse.builder()
	            .id(pm.getMovie().getId())
	            .title(pm.getMovie().getTitle())
	            .year(pm.getMovie().getYear())
	            .type(pm.getMovie().getType())
	            .poster(pm.getMovie().getPoster())
	            .director(pm.getMovie().getDirector())
	            .actors(pm.getMovie().getActors())
	            .movieBudget(pm.getMovie().getMovieBudget())
	            .leftOverBudget(pm.getMovie().getLeftOverBudget())
	            .sharePrice(pm.getMovie().getSharePrice())
	            .lotPrice(pm.getMovie().getLotPrice())
	            .sharesPerLot(pm.getMovie().getSharesPerLot())
	            .maxLots(pm.getMovie().getMaxLots())
	            .maxUserLots(pm.getMovie().getMaxUserLots())
	            .isProduced(pm.isProduced())
	            .isLiked(pm.isLiked())
	            .build()
	        )
	        .toList();
	}
	
	private ProducedMovie getOrCreateProducedMovie(User user, Movie movie) {

	    return producedMovieRepository
	        .findByUserIdAndMovieId(user.getId(), movie.getId())
	        .orElseGet(() -> {
	            ProducedMovie inv = new ProducedMovie();
	            inv.setUser(user);
	            inv.setMovie(movie);
	    	    inv.setProduced(true);

	            // defaults already set in entity
	            return inv;
	        });
	}

	private void validateUserCanBuyShares(ProducedMovie investment, Movie movie, int sharesToBuy) {

		int sharesPerLot = movie.getSharesPerLot();
		int maxUserLots = movie.getMaxUserLots();
		int maxSharesUserCanBuy = sharesPerLot * maxUserLots;
		
		if (investment.getId() == null) {
			if (sharesToBuy > maxSharesUserCanBuy) {
				throw new IllegalStateException("Max allowed shares per user: " + maxSharesUserCanBuy);
			}
		} else {
			int existingUserShareInMovie = investment.getShares();
			if (existingUserShareInMovie + sharesToBuy > maxSharesUserCanBuy) {
				throw new IllegalStateException("Max allowed shares per user: " + maxSharesUserCanBuy);
			}
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProducedMovie> getUserInvestmentForMovie(Long userId) {

		return producedMovieRepository.findByUserIdAndIsProducedTrue(userId);
		
	}


}