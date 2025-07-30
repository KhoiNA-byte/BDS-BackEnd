package com.blooddonation.blood_donation_support_system.enums;

public enum DonationRegistrationStatus {
    PENDING("Pending"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    REJECTED("Rejected"),
    CHECKED_IN("Checked In");



    private final String status;

    DonationRegistrationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
