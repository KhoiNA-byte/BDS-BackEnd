package com.blooddonation.blood_donation_support_system.enums;

public enum DonationType {
    WHOLE_BLOOD("Whole Blood Donation"),
    PLATELET("Platelet Donation");

    private final String type;

    DonationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
