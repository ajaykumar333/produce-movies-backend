package com.movies.produce_movies.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "omdb")
public class OmdbProperties {

    /**
     * OMDb API key. Inject via environment variable or external config.
     */
    @NotBlank
    private String apiKey;

    /**
     * OMDb base URL, defaulting to public API endpoint.
     */
    @NotBlank
    private String baseUrl;
}


