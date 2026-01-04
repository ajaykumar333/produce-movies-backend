package com.movies.produce_movies.jobs;

import com.movies.produce_movies.entity.Movie;
import com.movies.produce_movies.movies.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

/**
 * Nightly job that pulls a page of movies from OMDb for the current year and
 * stores any new items into the local database. Runs every day at 6:00 AM.
 */
@Component
public class OmdbDailySyncJob {

    private static final Logger log = LoggerFactory.getLogger(OmdbDailySyncJob.class);

    private final MovieService movieService;

    public OmdbDailySyncJob(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Cron format: second minute hour day-of-month month day-of-week
     * This runs every day at 06:00 AM server time.
     */
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void syncCurrentYearMovies() {
        int year = Year.now().getValue();
        int page = 1; // adjust if you want to pull additional pages

        try {
            List<Movie> movies = movieService.saveAllMoviesByYear(year, page);
            if (movies == null || movies.isEmpty()) {
                log.warn("OMDb sync: no results returned for year {}", year);
                return;
            }

            movies.forEach(this::saveIfNew);
            log.info("OMDb sync complete for year {}, page {}. Saved/checked {} items.", year, page, movies.size());
        } catch (Exception ex) {
            log.error("OMDb sync failed for year {}", year, ex);
        }
    }

    private void saveIfNew(Movie movie) {
        if (movie == null || movie.getImdbId() == null || movie.getImdbId().isBlank()) {
            return;
        }

        movieService.saveAllMoviesByYear(Integer.parseInt(movie.getYear()), 1);
    }
}
