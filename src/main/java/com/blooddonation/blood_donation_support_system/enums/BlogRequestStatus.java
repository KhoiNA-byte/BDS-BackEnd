package com.blooddonation.blood_donation_support_system.enums;

public enum BlogRequestStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private String status;

    BlogRequestStatus (String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
