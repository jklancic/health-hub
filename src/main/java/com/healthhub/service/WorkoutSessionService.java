package com.healthhub.service;

import com.healthhub.dto.WorkoutSessionRequest;
import com.healthhub.dto.WorkoutSessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkoutSessionService {

    WorkoutSessionResponse createSession(UUID userId, WorkoutSessionRequest request);

    WorkoutSessionResponse getSession(UUID userId, UUID sessionId);

    Page<WorkoutSessionResponse> getSessions(UUID userId, Pageable pageable);

    WorkoutSessionResponse updateSession(UUID userId, UUID sessionId, WorkoutSessionRequest request);

    void deleteSession(UUID userId, UUID sessionId);
}
