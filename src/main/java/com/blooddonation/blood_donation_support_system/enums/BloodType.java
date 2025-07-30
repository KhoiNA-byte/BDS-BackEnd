package com.blooddonation.blood_donation_support_system.enums;

public enum BloodType {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String type;

    BloodType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
