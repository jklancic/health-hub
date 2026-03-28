package com.healthhub.service.impl;

import com.healthhub.dto.WorkoutDTO;
import com.healthhub.dto.WorkoutPlanRequest;
import com.healthhub.dto.WorkoutPlanResponse;
import com.healthhub.entity.User;
import com.healthhub.entity.Workout;
import com.healthhub.entity.WorkoutPlan;
import com.healthhub.exception.ResourceNotFoundException;
import com.healthhub.repository.UserRepository;
import com.healthhub.repository.WorkoutPlanRepository;
import com.healthhub.repository.WorkoutSessionRepository;
import com.healthhub.service.WorkoutPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private static final Logger log = LoggerFactory.getLogger(WorkoutPlanServiceImpl.class);

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;

    @Autowired
    public WorkoutPlanServiceImpl(WorkoutPlanRepository workoutPlanRepository,
                                  WorkoutSessionRepository workoutSessionRepository,
                                  UserRepository userRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public WorkoutPlanResponse createPlan(UUID userId, WorkoutPlanRequest request) {
        log.debug("Creating workout plan for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with id " + userId);
                });

        WorkoutPlan plan = new WorkoutPlan();
        plan.setUser(user);
        plan.setName(request.name());
        applyWorkouts(plan, request);

        WorkoutPlan saved = workoutPlanRepository.save(plan);
        log.info("Created workout plan with ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public WorkoutPlanResponse getPlan(UUID userId, UUID planId) {
        log.debug("Fetching workout plan ID: {} for user ID: {}", planId, userId);
        WorkoutPlan plan = workoutPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> {
                    log.warn("Workout plan not found with ID: {}", planId);
                    return new ResourceNotFoundException("Workout plan not found with id " + planId);
                });
        return toResponse(plan);
    }

    @Override
    public List<WorkoutPlanResponse> getPlans(UUID userId) {
        log.debug("Fetching workout plans for user ID: {}", userId);
        return workoutPlanRepository.findTop10ByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WorkoutPlanResponse updatePlan(UUID userId, UUID planId, WorkoutPlanRequest request) {
        log.debug("Updating workout plan ID: {} for user ID: {}", planId, userId);
        WorkoutPlan plan = workoutPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> {
                    log.warn("Workout plan not found with ID: {}", planId);
                    return new ResourceNotFoundException("Workout plan not found with id " + planId);
                });

        plan.setName(request.name());
        plan.getWorkouts().clear();
        applyWorkouts(plan, request);

        WorkoutPlan updated = workoutPlanRepository.save(plan);
        log.info("Updated workout plan with ID: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deletePlan(UUID userId, UUID planId) {
        log.debug("Deleting workout plan ID: {} for user ID: {}", planId, userId);
        WorkoutPlan plan = workoutPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> {
                    log.warn("Workout plan not found with ID: {}", planId);
                    return new ResourceNotFoundException("Workout plan not found with id " + planId);
                });

        if (workoutSessionRepository.existsByWorkoutPlanId(planId)) {
            log.warn("Cannot delete workout plan ID: {} — sessions exist", planId);
            throw new IllegalStateException("Cannot delete a workout plan that has recorded sessions");
        }

        workoutPlanRepository.delete(plan);
        log.info("Deleted workout plan with ID: {}", planId);
    }

    private void applyWorkouts(WorkoutPlan plan, WorkoutPlanRequest request) {
        if (request.workouts() == null) {
            return;
        }
        List<WorkoutDTO> dtos = request.workouts();
        for (int i = 0; i < dtos.size(); i++) {
            WorkoutDTO dto = dtos.get(i);
            Workout workout = new Workout();
            workout.setWorkoutPlan(plan);
            workout.setName(dto.name());
            workout.setSets(dto.sets());
            workout.setRepsMin(dto.repsMin());
            workout.setRepsMax(dto.repsMax());
            workout.setRestSeconds(dto.restSeconds());
            workout.setSortOrder(i);
            plan.getWorkouts().add(workout);
        }
    }

    private WorkoutPlanResponse toResponse(WorkoutPlan plan) {
        List<WorkoutDTO> workouts = plan.getWorkouts().stream()
                .map(w -> new WorkoutDTO(w.getId(), w.getName(), w.getSets(), w.getRepsMin(), w.getRepsMax(), w.getRestSeconds()))
                .toList();
        return new WorkoutPlanResponse(plan.getId(), plan.getName(), workouts);
    }
}
