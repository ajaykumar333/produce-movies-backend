package com.movies.produce_movies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${front-end.url}")
    private String frontEndUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allowed frontend origins
        config.setAllowedOrigins(List.of(
                frontEndUrl, 
                "https://produce-movies-frontend.onrender.com",
                "https://bright-axolotl-15ed9f.netlify.app/"
        ));

        // ✅ HTTP methods
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // ✅ Required headers for JWT
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        // ✅ Allow cookies / Authorization header
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // ✅ Apply to all endpoints
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
