package com.blooddonation.blood_donation_support_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleMapsDistanceResponse {
    private String status;
    private List<Row> rows;
    
    @JsonProperty("origin_addresses")
    private List<String> originAddresses;
    
    @JsonProperty("destination_addresses")
    private List<String> destinationAddresses;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Row {
        private List<Element> elements;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Element {
        private String status;
        private Distance distance;
        private Duration duration;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Distance {
        private String text;
        private Long value; // in meters
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Duration {
        private String text;
        private Long value; // in seconds
    }
}
