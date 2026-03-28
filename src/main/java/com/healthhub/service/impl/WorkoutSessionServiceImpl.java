package com.healthhub.service.impl;

import com.healthhub.dto.WorkoutSessionRequest;
import com.healthhub.dto.WorkoutSessionResponse;
import com.healthhub.dto.WorkoutTrackDTO;
import com.healthhub.entity.User;
import com.healthhub.entity.Workout;
import com.healthhub.entity.WorkoutPlan;
import com.healthhub.entity.WorkoutSession;
import com.healthhub.entity.WorkoutTrack;
import com.healthhub.exception.ResourceNotFoundException;
import com.healthhub.repository.UserRepository;
import com.healthhub.repository.WorkoutPlanRepository;
import com.healthhub.repository.WorkoutRepository;
import com.healthhub.repository.WorkoutSessionRepository;
import com.healthhub.service.WorkoutSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutSessionServiceImpl implements WorkoutSessionService {

    private static final Logger log = LoggerFactory.getLogger(WorkoutSessionServiceImpl.class);

    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    @Autowired
    public WorkoutSessionServiceImpl(WorkoutSessionRepository workoutSessionRepository,
                                     WorkoutPlanRepository workoutPlanRepository,
                                     WorkoutRepository workoutRepository,
                                     UserRepository userRepository) {
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public WorkoutSessionResponse createSession(UUID userId, WorkoutSessionRequest request) {
        log.debug("Creating workout session for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with id " + userId);
                });

        WorkoutPlan plan = workoutPlanRepository.findByIdAndUserId(request.workoutPlanId(), userId)
                .orElseThrow(() -> {
                    log.warn("Workout plan not found with ID: {}", request.workoutPlanId());
                    return new ResourceNotFoundException("Workout plan not found with id " + request.workoutPlanId());
                });

        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setWorkoutPlan(plan);
        session.setStartedAt(request.startedAt());
        session.setFinishedAt(request.finishedAt());
        session.setDeload(request.deload());
        applyTracks(session, request);

        WorkoutSession saved = workoutSessionRepository.save(session);
        log.info("Created workout session with ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public WorkoutSessionResponse getSession(UUID userId, UUID sessionId) {
        log.debug("Fetching workout session ID: {} for user ID: {}", sessionId, userId);
        WorkoutSession session = workoutSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> {
                    log.warn("Workout session not found with ID: {}", sessionId);
                    return new ResourceNotFoundException("Workout session not found with id " + sessionId);
                });
        return toResponse(session);
    }

    @Override
    public Page<WorkoutSessionResponse> getSessions(UUID userId, Pageable pageable) {
        log.debug("Fetching workout sessions for user ID: {}", userId);
        return workoutSessionRepository.findByUserIdOrderByDateDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public WorkoutSessionResponse updateSession(UUID userId, UUID sessionId, WorkoutSessionRequest request) {
        log.debug("Updating workout session ID: {} for user ID: {}", sessionId, userId);
        WorkoutSession session = workoutSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> {
                    log.warn("Workout session not found with ID: {}", sessionId);
                    return new ResourceNotFoundException("Workout session not found with id " + sessionId);
                });

        session.setDeload(request.deload());
        session.setStartedAt(request.startedAt());
        session.setFinishedAt(request.finishedAt());
        session.getTracks().clear();
        applyTracks(session, request);

        WorkoutSession updated = workoutSessionRepository.save(session);
        log.info("Updated workout session with ID: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSession(UUID userId, UUID sessionId) {
        log.debug("Deleting workout session ID: {} for user ID: {}", sessionId, userId);
        WorkoutSession session = workoutSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> {
                    log.warn("Workout session not found with ID: {}", sessionId);
                    return new ResourceNotFoundException("Workout session not found with id " + sessionId);
                });
        workoutSessionRepository.delete(session);
        log.info("Deleted workout session with ID: {}", sessionId);
    }

    private void applyTracks(WorkoutSession session, WorkoutSessionRequest request) {
        if (request.tracks() == null) {
            return;
        }
        for (WorkoutTrackDTO dto : request.tracks()) {
            Workout workout = workoutRepository.findById(dto.workoutId())
                    .orElseThrow(() -> {
                        log.warn("Workout not found with ID: {}", dto.workoutId());
                        return new ResourceNotFoundException("Workout not found with id " + dto.workoutId());
                    });
            WorkoutTrack track = new WorkoutTrack();
            track.setSession(session);
            track.setWorkout(workout);
            track.setSets(dto.sets());
            track.setReps(dto.reps());
            track.setWeight(dto.weight());
            session.getTracks().add(track);
        }
    }

    private WorkoutSessionResponse toResponse(WorkoutSession session) {
        List<WorkoutTrackDTO> tracks = session.getTracks().stream()
                .map(t -> new WorkoutTrackDTO(
                        t.getId(),
                        t.getWorkout().getId(),
                        t.getWorkout().getName(),
                        t.getSets(),
                        t.getReps(),
                        t.getWeight()))
                .toList();
        return new WorkoutSessionResponse(
                session.getId(),
                session.getWorkoutPlan().getId(),
                session.getWorkoutPlan().getName(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.isDeload(),
                tracks);
    }
}
