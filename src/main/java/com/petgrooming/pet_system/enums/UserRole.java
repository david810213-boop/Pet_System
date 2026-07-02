package com.petgrooming.pet_system.enums;

public enum UserRole {
    ADMIN("管理者"),
    STAFF("美容師"),
    CUSTOMER("消費者");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }
}
