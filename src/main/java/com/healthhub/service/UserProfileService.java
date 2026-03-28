package com.healthhub.service;

import java.util.UUID;

import com.healthhub.dto.UserProfileDTO;
import com.healthhub.entity.UserProfile;

public interface UserProfileService {
    
    UserProfile getUserProfile(UUID userId);

    UserProfile updatUserProfile(UserProfileDTO userProfile);
}
