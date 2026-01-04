//package com.movies.produce_movies.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//public class JwtUtils {
//    
//    @Value("${app.jwtSecret:mySecretKey}")
//    private String jwtSecret;
//    
//    @Value("${app.jwtExpirationMs:86400000}")
//    private int jwtExpirationMs;
//    
//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
//    }
//    
//    public String generateJwtToken(Authentication authentication) {
//        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
//        
//        return Jwts.builder()
//                .setSubject(userPrincipal.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//    
//    public String getUserNameFromJwtToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//    
//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
//            return true;
//        } catch (MalformedJwtException e) {
//            System.err.println("Invalid JWT token: " + e.getMessage());
//        } catch (ExpiredJwtException e) {
//            System.err.println("JWT token is expired: " + e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            System.err.println("JWT token is unsupported: " + e.getMessage());
//        } catch (IllegalArgumentException e) {
//            System.err.println("JWT claims string is empty: " + e.getMessage());
//        }
//        return false;
//    }
//}


package com.movies.produce_movies.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMs}")
    private int jwtExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal =
            (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
            .setSubject(userPrincipal.getUsername())
            .claim("id", userPrincipal.getId())
            .claim("role",
                userPrincipal.getAuthorities().iterator().next().getAuthority())
            .setIssuedAt(new Date())
            .setExpiration(
                new Date(System.currentTimeMillis() + jwtExpirationMs)
            )
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken)
                .getBody();
            
            // Check if token is expired
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }
}
