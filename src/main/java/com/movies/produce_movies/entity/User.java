package com.movies.produce_movies.entity;

import java.util.List;

import com.movies.produce_movies.constants.Roles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 80)
    private String fullName;

    @Column(nullable = false, length = 32)
    private String role = Roles.ROLE_USER;
    
    @Column(nullable = true, length = 32)
    private Double portFolioWorth;
     
}


