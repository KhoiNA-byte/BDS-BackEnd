package com.blooddonation.blood_donation_support_system.enums;

public enum MedicalCondition {
    CHRONIC_STABLE_CONDITIONS("Chronic Stable Conditions", "Iron deficiency anemia, chronic kidney disease, regular transfusion thalassemia", Urgency.LOW),
    PLANNED_SURGERY("Planned Surgery", "Scheduled surgery, pre-operative blood preparation, elective procedures", Urgency.LOW),
    MODERATE_ANEMIA_OR_BLEEDING("Moderate Anemia or Bleeding", "Moderate bleeding, gastrointestinal bleeding, moderate anemia", Urgency.MEDIUM),
    ACUTE_HEMORRHAGE("Acute Hemorrhage", "Postpartum hemorrhage, acute gastrointestinal bleeding, sudden blood loss", Urgency.HIGH),
    TRAUMA_AND_MAJOR_SURGERY("Trauma and Major Surgery", "Traffic accidents, heart/liver surgery, massive blood loss", Urgency.HIGH),
    OTHERS("Others", "Other medical conditions requiring blood transfusion", Urgency.MEDIUM);

    private final String condition;
    private final String description;
    private final Urgency urgencyLevel;

    MedicalCondition(String condition, String description, Urgency urgencyLevel) {
        this.condition = condition;
        this.description = description;
        this.urgencyLevel = urgencyLevel;
    }

    public String getCondition() {
        return condition;
    }

    public String getDescription() {
        return description;
    }

    public Urgency getUrgencyLevel() {
        return urgencyLevel;
    }
}
