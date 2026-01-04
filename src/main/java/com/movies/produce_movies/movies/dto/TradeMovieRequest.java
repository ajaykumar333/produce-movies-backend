package com.movies.produce_movies.movies.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeMovieRequest {
    private Long userId;
    private Long movieId;
    private Integer shares;
}
