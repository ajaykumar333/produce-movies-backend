package com.movies.produce_movies.movies.service.impl;

import com.movies.produce_movies.entity.User;
import com.movies.produce_movies.movies.service.UserService;
import com.movies.produce_movies.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User user) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setEmail(user.getEmail());
                    existing.setFullName(user.getFullName());
                    if (user.getPasswordHash() != null) {
                        existing.setPasswordHash(user.getPasswordHash());
                    }
                    existing.setRole(user.getRole());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}


