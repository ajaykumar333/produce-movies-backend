package com.movies.produce_movies.security;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    public void clearExpiredTokens() {
        // In production, implement with Redis or database
        // For now, tokens will be cleared on server restart
    }
}