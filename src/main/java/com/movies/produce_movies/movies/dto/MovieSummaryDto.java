package com.movies.produce_movies.movies.dto;

import com.movies.produce_movies.entity.ProducedMovie;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
@Data
public class MovieSummaryDto {
    private Long id;
    private String imdbId;
    private String title;
    private String year;
    private String type;
    private String poster;
    private String director;
    private String actors;
    private Double movieBudget;
	private Double leftOverBudget;
	private Double sharePrice;
	private Double lotPrice;
	private Integer sharesPerLot;
	private Integer maxLots;
	private Integer maxUserLots;
	
}


