package com.blooddonation.blood_donation_support_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkBloodUnitRecordDto {
    @Valid
    @Size(min = 1, message = "At least one blood unit record is required")
    private List<SingleBloodUnitRecordDto> singleBloodUnitRecords;
}
