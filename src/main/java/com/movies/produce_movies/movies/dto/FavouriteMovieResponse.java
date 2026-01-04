package com.movies.produce_movies.movies.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
@Data
public class FavouriteMovieResponse {
    private Long id;
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
	private Boolean isProduced;
	private Boolean isLiked;
	
}


