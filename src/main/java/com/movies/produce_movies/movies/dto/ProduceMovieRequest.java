package com.movies.produce_movies.movies.dto;

import lombok.Data;

@Data
public class ProduceMovieRequest {
	private Long userId;
	private Long movieId;
    private Double shares;
    private Boolean isLiked;
}

