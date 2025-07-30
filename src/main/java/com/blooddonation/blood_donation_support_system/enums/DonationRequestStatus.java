package com.blooddonation.blood_donation_support_system.enums;

public enum DonationRequestStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private String status;

    DonationRequestStatus (String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
