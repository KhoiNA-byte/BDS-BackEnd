package com.blooddonation.blood_donation_support_system.enums;

public enum AccountStatus {
    ENABLE("Enable"),
    DISABLE("Disable");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
