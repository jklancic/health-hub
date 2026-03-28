package com.healthhub.service;

import com.healthhub.dto.UserRequest;
import com.healthhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User createUser(UserRequest userRequest);

    Optional<User> getUserById(UUID id);

    User updateUser(UUID id, User userDetails);

    void deleteUser(UUID id);

    Page<User> getAllUsers(Pageable pageable);

    List<User> getAllUsers();

    Optional<User> findByUsername(String username);
}
