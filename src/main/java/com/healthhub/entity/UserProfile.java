package com.healthhub.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private Instant creationDate;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "height")
    private Double height;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "units")
    private SystemOfUnits units;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
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

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = Instant.now();
        }
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
