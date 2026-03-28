package com.healthhub.service.impl;

import com.healthhub.dto.UserRequest;
import com.healthhub.entity.Role;
import com.healthhub.entity.User;
import com.healthhub.exception.DuplicateResourceException;
import com.healthhub.exception.ResourceNotFoundException;
import com.healthhub.repository.UserRepository;
import com.healthhub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(UserRequest userRequest) {
        log.debug("Creating new user with username: {}", userRequest.getUsername());

        // Check for duplicate username
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            log.warn("Attempt to create user with duplicate username: {}", userRequest.getUsername());
            throw new DuplicateResourceException("Username '" + userRequest.getUsername() + "' is already taken");
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            log.warn("Attempt to create user with duplicate email: {}", userRequest.getEmail());
            throw new DuplicateResourceException("Email '" + userRequest.getEmail() + "' is already registered");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        // Ensure at least USER role is assigned
        Set<Role> roles = userRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Role.USER); // Always add USER role, when nothing is passed
        }
        user.setRoles(roles);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        log.info("Successfully created user: {} with ID: {}", savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(UUID id, User userDetails) {
        log.debug("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with id " + id);
                });

        // Check for duplicate username if it's being changed
        if (!user.getUsername().equals(userDetails.getUsername()) &&
            userRepository.existsByUsername(userDetails.getUsername())) {
            log.warn("Attempt to update user {} with duplicate username: {}", id, userDetails.getUsername());
            throw new DuplicateResourceException("Username '" + userDetails.getUsername() + "' is already taken");
        }

        // Check for duplicate email if it's being changed
        if (!user.getEmail().equals(userDetails.getEmail()) &&
            userRepository.existsByEmail(userDetails.getEmail())) {
            log.warn("Attempt to update user {} with duplicate email: {}", id, userDetails.getEmail());
            throw new DuplicateResourceException("Email '" + userDetails.getEmail() + "' is already registered");
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setRoles(userDetails.getRoles());
        user.setAccountNonExpired(userDetails.isAccountNonExpired());
        user.setAccountNonLocked(userDetails.isAccountNonLocked());
        user.setCredentialsNonExpired(userDetails.isCredentialsNonExpired());
        user.setEnabled(userDetails.isEnabled());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Successfully updated user: {} (ID: {})", updatedUser.getUsername(), id);
        return updatedUser;
    }

    @Override
    public void deleteUser(UUID id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
        log.debug("Successfully deleted user with ID: {}", id);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
