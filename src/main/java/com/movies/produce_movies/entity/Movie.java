package com.movies.produce_movies.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imdb_id", length = 20, unique = true)
    private String imdbId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 4)
    private String year;

    @Column(columnDefinition = "TEXT")
    private String plot;

    @Column(columnDefinition = "TEXT")
    private String ratingsJson;

    @Column(columnDefinition = "TEXT")
    private String awards;

    @Column(length = 32)
    private String type; // e.g., "movie", "series"

    @Column(length = 32)
    private String rated; // e.g., "PG"

    @Column(length = 64)
    private String released; // e.g., "25 May 1977"

    @Column(length = 32)
    private String runtime; // e.g., "121 min"

    @Column(length = 512)
    private String genre; // e.g., "Action, Adventure, Fantasy"

    @Column(length = 512)
    private String director;

    @Column(length = 512)
    private String writer;

    @Column(length = 512)
    private String actors;

    @Column(length = 128)
    private String language;

    @Column(length = 128)
    private String country;

    @Column(length = 16)
    private String metascore;

    @Column(length = 16)
    private String imdbRating;

    @Column(length = 32)
    private String imdbVotes;

    @Column(length = 128)
    private String dvd; // DVD release date

    @Column(length = 128)
    private String boxOffice;

    @Column(length = 255)
    private String production;

    @Column(length = 255)
    private String website;

    @Column(length = 512)
    private String poster;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
    
    private Double movieBudget;
    
    private Double leftOverBudget;
    
    private Boolean isActive = true;
    
    // Share and lot information
    private Double sharePrice; // Price per share (e.g., 100 rupees)
    private Double lotPrice; // Price per lot
    private Integer sharesPerLot; // Number of shares in one lot (e.g., 100)
    private Integer maxLots; // Maximum lots available for this movie
    private Integer maxUserLots; // Maximum lots a user can buy (e.g., 2)

}
