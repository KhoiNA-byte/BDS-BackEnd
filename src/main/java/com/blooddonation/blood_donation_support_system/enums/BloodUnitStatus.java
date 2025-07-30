package com.blooddonation.blood_donation_support_system.enums;

public enum BloodUnitStatus {
    PENDING("Pending"),
    COMPLETED("Completed");

    private final String status;

    BloodUnitStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
