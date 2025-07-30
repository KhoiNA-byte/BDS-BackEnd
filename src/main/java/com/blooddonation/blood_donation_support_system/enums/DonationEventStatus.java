package com.blooddonation.blood_donation_support_system.enums;

public enum DonationEventStatus {
    AVAILABLE("Available"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private String status;

    DonationEventStatus(String status) {
        this.status = status;
    }

    public String getStatus() {return status;}
}
