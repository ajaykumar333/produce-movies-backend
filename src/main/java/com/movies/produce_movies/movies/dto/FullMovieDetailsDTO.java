package com.movies.produce_movies.movies.dto;

import java.time.Instant;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FullMovieDetailsDTO {

	private Long id;
	private String imdbId;
	private String title;
	private String year;
	private String type;
	private String poster;
	private String plot;
	private String ratingsJson;
	private String awards;
	private String rated;
	private String released;
	private String runtime;
	private String genre;
	private String director;
	private String writer;
	private String actors;
	private String language;
	private String country;
	private String metascore;
	private String imdbRating;
	private String imdbVotes;
	private String dvd;
	private String boxOffice;
	private String production;
	private String website;
	private Instant createdAt;
	private Instant updatedAt;

	// Budget and share information
	private Double movieBudget;
	private Double leftOverBudget;
	private Double sharePrice;
	private Double lotPrice;
	private Integer sharesPerLot;
	private Integer maxLots;
	private Integer maxUserLots;

}
