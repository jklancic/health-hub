package com.healthhub.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class BodyMeasurement {

    private UUID id;
    private Instant creationDate;

    private LocalDate dateTaken;
    private Double weight;
    private Double waist;

    public BodyMeasurement() {
        
    }

    public BodyMeasurement(Instant creationDate, LocalDate dateTaken, Double weight, Double waist) {
        this.creationDate = creationDate;
        this.dateTaken = dateTaken;
        this.weight = weight;
        this.waist = waist;
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

    public LocalDate getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(LocalDate dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWaist() {
        return waist;
    }

    public void setWaist(Double waist) {
        this.waist = waist;
    }

    
}
