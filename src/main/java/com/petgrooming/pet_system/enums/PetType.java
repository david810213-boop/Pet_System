package com.petgrooming.pet_system.enums;

public enum PetType {
    DOG("狗"),
    CAT("貓"),
    OTHER("其他");

    private final String description;

    PetType(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }
}
