package com.blooddonation.blood_donation_support_system.enums;

public enum ComponentType {
    WHOLE_BLOOD("Whole Blood"),
    PLASMA("Plasma"),
    PLATELETS("Platelets"),
    RED_BLOOD_CELLS("Red Blood Cells");

    private final String type;

    ComponentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
