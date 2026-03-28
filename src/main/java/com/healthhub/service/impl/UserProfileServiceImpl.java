package com.healthhub.service.impl;

import com.healthhub.dto.UserProfileDTO;
import com.healthhub.entity.UserProfile;
import com.healthhub.exception.ResourceNotFoundException;
import com.healthhub.repository.UserProfileRepository;
import com.healthhub.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile getUserProfile(UUID userId) {
        log.debug("Fetching profile for user ID: {}", userId);
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Profile not found for user with id " + userId);
                });
    }

    @Override
    public UserProfile updatUserProfile(UserProfileDTO userProfile) {
        log.debug("Updating profile with ID: {}", userProfile.id());
        UserProfile existing = userProfileRepository.findById(userProfile.id())
                .orElseThrow(() -> {
                    log.warn("Profile not found with ID: {}", userProfile.id());
                    return new ResourceNotFoundException("Profile not found with id " + userProfile.id());
                });

        existing.setBirthDate(userProfile.birthDate());
        existing.setHeight(userProfile.height());
        existing.setGender(userProfile.gender());
        existing.setUnits(userProfile.units());

        UserProfile updated = userProfileRepository.save(existing);
        log.info("Successfully updated profile with ID: {}", updated.getId());
        return updated;
    }
}
