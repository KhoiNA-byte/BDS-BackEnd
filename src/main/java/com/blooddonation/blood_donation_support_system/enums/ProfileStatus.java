package com.blooddonation.blood_donation_support_system.enums;

public enum ProfileStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable");

    private final String status;

    ProfileStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
