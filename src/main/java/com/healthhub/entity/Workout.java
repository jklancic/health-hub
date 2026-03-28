package com.healthhub.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int sets;

    @Column(name = "reps_min", nullable = false)
    private int repsMin;

    @Column(name = "reps_max", nullable = false)
    private int repsMax;

    @Column(name = "rest_seconds", nullable = false)
    private int restSeconds;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public Workout() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WorkoutPlan getWorkoutPlan() {
        return workoutPlan;
    }

    public void setWorkoutPlan(WorkoutPlan workoutPlan) {
        this.workoutPlan = workoutPlan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRepsMin() {
        return repsMin;
    }

    public void setRepsMin(int repsMin) {
        this.repsMin = repsMin;
    }

    public int getRepsMax() {
        return repsMax;
    }

    public void setRepsMax(int repsMax) {
        this.repsMax = repsMax;
    }

    public int getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(int restSeconds) {
        this.restSeconds = restSeconds;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
