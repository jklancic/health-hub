package com.healthhub.controller;

import com.healthhub.dto.WorkoutPlanRequest;
import com.healthhub.dto.WorkoutPlanResponse;
import com.healthhub.service.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @Autowired
    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @PostMapping
    public ResponseEntity<WorkoutPlanResponse> createPlan(
            @PathVariable UUID userId,
            @RequestBody WorkoutPlanRequest request) {
        WorkoutPlanResponse created = workoutPlanService.createPlan(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping
    public ResponseEntity<List<WorkoutPlanResponse>> getPlans(@PathVariable UUID userId) {
        return ResponseEntity.ok(workoutPlanService.getPlans(userId));
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping("/{planId}")
    public ResponseEntity<WorkoutPlanResponse> getPlan(
            @PathVariable UUID userId,
            @PathVariable UUID planId) {
        return ResponseEntity.ok(workoutPlanService.getPlan(userId, planId));
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @PutMapping("/{planId}")
    public ResponseEntity<WorkoutPlanResponse> updatePlan(
            @PathVariable UUID userId,
            @PathVariable UUID planId,
            @RequestBody WorkoutPlanRequest request) {
        return ResponseEntity.ok(workoutPlanService.updatePlan(userId, planId, request));
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable UUID userId,
            @PathVariable UUID planId) {
        workoutPlanService.deletePlan(userId, planId);
        return ResponseEntity.noContent().build();
    }
}
