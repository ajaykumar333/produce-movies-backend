package com.movies.produce_movies.movies.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MovieSearchResponseDto {
    List<MovieSummaryDto> results;
    int page;
    int totalResults;
}


