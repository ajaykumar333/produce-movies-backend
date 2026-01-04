package com.movies.produce_movies.movies.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMovieInvestmentDTO {

    private Long movieId;
    private String movieTitle;

    private Long userId;
    private String userName;

    private Integer shares;
    private Integer lots;
    private Double investedAmount;

    private Double sharePrice;
    private Integer sharesPerLot;
}
