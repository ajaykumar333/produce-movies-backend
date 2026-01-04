package com.movies.produce_movies.movies.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieBudgetDTO {
    private Long movieId;
    private Long movieBudget;
    private Double leftOverBudget;
    private Double sharePrice;
    private Integer sharesPerLot;
    private Integer maxLots;
    private Integer maxUserLots;
}
