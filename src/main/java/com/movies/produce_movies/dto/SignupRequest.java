package com.movies.produce_movies.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Size(max = 80)
    private String fullName;
    
    @NotBlank
    @Email
    @Size(max = 120)
    private String email;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}