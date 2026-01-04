package com.movies.produce_movies.controller;

import com.movies.produce_movies.dto.JwtResponse;
import com.movies.produce_movies.dto.LoginRequest;
import com.movies.produce_movies.dto.SignupRequest;
import com.movies.produce_movies.entity.User;
import com.movies.produce_movies.repository.UserRepository;
import com.movies.produce_movies.security.JwtUtils;
import com.movies.produce_movies.security.TokenBlacklistService;
import com.movies.produce_movies.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	TokenBlacklistService tokenBlacklistService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtUtils.generateJwtToken(authentication);

		UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
				userDetails.getFullName(), userDetails.getAuthorities().iterator().next().getAuthority()));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body("Error: Email is already in use!");
		}

		// Create new user's account
		User user = new User();
		user.setFullName(signUpRequest.getFullName());
		user.setEmail(signUpRequest.getEmail());
		user.setPasswordHash(encoder.encode(signUpRequest.getPassword()));

		userRepository.save(user);

		return ResponseEntity.ok("User registered successfully!");
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			tokenBlacklistService.blacklistToken(token);
		}
		SecurityContextHolder.clearContext();
		return ResponseEntity.ok("User logged out successfully!");
	}

	@PostMapping("/validate-token")
	public ResponseEntity<?> validateToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			if (!tokenBlacklistService.isTokenBlacklisted(token) && 
				jwtUtils.validateJwtToken(token) && !jwtUtils.isTokenExpired(token)) {
				return ResponseEntity.ok("Token is valid");
			}
		}
		return ResponseEntity.status(401).body("Token is invalid or expired");
	}
	
}