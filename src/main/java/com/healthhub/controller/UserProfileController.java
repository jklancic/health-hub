package com.healthhub.controller;

import com.healthhub.dto.UserProfileDTO;
import com.healthhub.entity.UserProfile;
import com.healthhub.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable UUID userId) {
        UserProfile profile = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @PutMapping
    public ResponseEntity<UserProfile> updateUserProfile(
            @PathVariable UUID userId,
            @RequestBody UserProfileDTO userProfileDTO) {
        UserProfile updated = userProfileService.updatUserProfile(userProfileDTO);
        return ResponseEntity.ok(updated);
    }
}
