package com.blooddonation.blood_donation_support_system.converters;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

@Converter
public class DonationEventDtoConverter implements AttributeConverter<DonationEventDto, String> {
    private final ObjectMapper objectMapper;

    public DonationEventDtoConverter() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public String convertToDatabaseColumn(DonationEventDto attribute) {
        try {
            return attribute != null ? objectMapper.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting DonationEventDto to JSON", e);
        }
    }

    @Override
    public DonationEventDto convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? objectMapper.readValue(dbData, DonationEventDto.class) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to DonationEventDto", e);
        }
    }
}