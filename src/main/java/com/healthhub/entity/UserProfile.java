package com.healthhub.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;

public class UserProfile {

    private UUID id;
    private Instant creationDate;

    private LocalDate birthDate;
    private Double height;
    private Gender gender;
    private SystemOfUnits units;


    @Column(name = "user_id", nullable = false)
    private User user;
    
    public UserProfile() {
    }

    public UserProfile(Instant creationDate, LocalDate birthDate, Double height, Gender gender,
            SystemOfUnits units) {
        this.creationDate = creationDate;
        this.birthDate = birthDate;
        this.height = height;
        this.gender = gender;
        this.units = units;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public SystemOfUnits getUnits() {
        return units;
    }

    public void setUnits(SystemOfUnits units) {
        this.units = units;
    }
}
