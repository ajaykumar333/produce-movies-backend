package com.movies.produce_movies.movies.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserHoldingsDTO {
   
	private Integer shares;
    private Integer lots;
    private Double investedAmount;
}
