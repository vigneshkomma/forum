package com.example.forum.service;

import com.example.forum.domain.User;
import com.example.forum.exception.DuplicateAccountException;
import com.example.forum.exception.ResourceNotFoundException;
import com.example.forum.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String username, String email, String rawPassword, String displayName) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (users.existsByUsername(normalizedUsername)) {
            throw new DuplicateAccountException("That username is already taken");
        }
        if (users.existsByEmail(normalizedEmail)) {
            throw new DuplicateAccountException("That email is already registered");
        }

        String display = (displayName == null || displayName.isBlank())
                ? normalizedUsername
                : displayName.trim();

        User user = new User(
                normalizedUsername,
                normalizedEmail,
                passwordEncoder.encode(rawPassword),
                display
        );

        try {
            return users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            // Covers the race between the exists-checks above and the insert.
            throw new DuplicateAccountException("That username or email is already registered");
        }
    }

    public User getById(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
    }

    public User getByUsername(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User " + username + " not found"));
    }
}
