package com.blooddonation.blood_donation_support_system.enums;

public enum BloodRequestStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    FAILED("Failed"),
    FULFILLED("Fulfilled");

    private final String status;

    BloodRequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
