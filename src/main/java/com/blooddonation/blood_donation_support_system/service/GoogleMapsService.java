package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.GoogleMapsDistanceResponse;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoogleMapsService {

    @Value("${SPRING_GOOGLE_MAPS_API_KEY}")
    private String apiKey;

    @Value("${STREET_ADDRESS}")
    private String streetAddress;

    @Value("${DISTRICT}")
    private String district;

    @Value("${CITY}")
    private String city;

    @Value("${STATE}")
    private String state;

    @Autowired
    private RestTemplate restTemplate;

    public GoogleMapsDistanceResponse calculateDistance(Profile profile) {
        validateApiKey();

        String origin = buildProfileAddressForGoogleMaps(profile);
        String destination = buildMedicalFacilityAddress();

        String url = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/distancematrix/json")
                .queryParam("origins", origin)
                .queryParam("destinations", destination)
                .queryParam("key", apiKey)
                .queryParam("mode", "driving")
                .queryParam("language", "en")
                .queryParam("units", "metric")
                .build()
                .encode()
                .toUriString();
//            String url = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/distancematrix/json")
//                    .queryParam("origins", origin)         // Do NOT pre-encode 'origin'
//                    .queryParam("destinations", destination)
//                    .queryParam("key", apiKey)
//                    .encode()
//                    .toUriString();// let Spring encode it
        log.info("RAW ORIGIN: '{}'", origin);
        log.info("RAW DEST: '{}'", destination);

        log.info("Calling Google Maps API for profile ID: {} with encoded URL: {}", profile.getId(), url);
        String rawJson = restTemplate.getForObject(url, String.class);
        log.info("Google Maps raw response: {}", rawJson);
        GoogleMapsDistanceResponse response = restTemplate.getForObject(url, GoogleMapsDistanceResponse.class);

        // Debug logging
        if (response != null) {
            log.debug("Raw API Response - Status: {}, Origin addresses: {}, Destination addresses: {}",
                    response.getStatus(), response.getOriginAddresses(), response.getDestinationAddresses());
        }

        if (response != null && "OK".equals(response.getStatus())) {
            log.info("Successfully calculated distance for profile ID: {}", profile.getId());
            return response;
        } else {
            String errorStatus = response != null ? response.getStatus() : "null response";
            log.error("Google Maps API returned error status: {} for profile ID: {}", errorStatus, profile.getId());

            // Handle specific error cases
            if ("REQUEST_DENIED".equals(errorStatus)) {
                throw new RuntimeException("Google Maps API request denied. Check API key, billing, and API restrictions.");
            } else if ("OVER_QUERY_LIMIT".equals(errorStatus)) {
                throw new RuntimeException("Google Maps API query limit exceeded.");
            } else if ("ZERO_RESULTS".equals(errorStatus)) {
                throw new RuntimeException("No route found between the addresses.");
            } else {
                throw new RuntimeException("Failed to calculate distance: " + errorStatus);
            }
        }

//        } catch (Exception e) {
//            log.error("Error calculating distance for profile ID: {}", profile.getId(), e);
//            throw new RuntimeException("Error calculating distance: " + e.getMessage(), e);
//        }
    }

    private String buildProfileAddressForGoogleMaps(Profile profile) {
        StringBuilder address = new StringBuilder();

        // Format: street_address + ward + district + city + country
        if (profile.getAddress() != null && !profile.getAddress().trim().isEmpty()) {
            String streetAddress = profile.getAddress().trim();
            if (streetAddress.contains("/")) streetAddress = streetAddress.replaceFirst(" ", "+");
            streetAddress = removeVietnameseDiacritics(streetAddress)
                    .replace(" ", "_");
            address.append(streetAddress);
        }

        if (profile.getWard() != null && !profile.getWard().trim().isEmpty()) {
            if (address.length() > 0) address.append("+");
            String ward = removeVietnameseDiacritics(profile.getWard().trim())
                    .replace(" ", "_");
            address.append(ward);
        }

        if (profile.getDistrict() != null && !profile.getDistrict().trim().isEmpty()) {
            if (address.length() > 0) address.append("+");
            String district = removeVietnameseDiacritics(profile.getDistrict().trim())
                    .replace(" ", "_");
            address.append(district);
        }

        if (profile.getCity() != null && !profile.getCity().trim().isEmpty()) {
            if (address.length() > 0) address.append("+");
            String city = removeVietnameseDiacritics(profile.getCity().trim())
                    .replace(" ", "_");
            address.append(city);
        }

        // Add country
        if (address.length() > 0) address.append("+");
        address.append("Vietnam");

        return address.toString();
    }


    private String buildMedicalFacilityAddress() {
        StringBuilder address = new StringBuilder();

        // Format: street_address + district + city + country
        if (streetAddress != null && !streetAddress.trim().isEmpty()) {
            String street = removeVietnameseDiacritics(streetAddress.trim())
                    .replace("Duong", "Duong")
                    .replace("Hong Bang", "Hong_Bang")
                    .replace(" ", "_");
            address.append(street);
        }

        if (district != null && !district.trim().isEmpty()) {
            if (address.length() > 0) address.append("+");
            String dist = removeVietnameseDiacritics(district.trim())
                    .replace("Quan ", "Quan_")
                    .replace("Quan", "Quan_")
                    .replace(" ", "_");
            address.append(dist);
        }

        if (city != null && !city.trim().isEmpty()) {
            if (address.length() > 0) address.append("+");
            String cityName = removeVietnameseDiacritics(city.trim())
                    .replace("Thanh pho ", "Thanh_pho_")
                    .replace("Thanh pho", "Thanh_pho_")
                    .replace("Ho Chi Minh", "Ho_Chi_Minh")
                    .replace(" ", "_");
            address.append(cityName);
        }

        if (state != null && !state.trim().isEmpty()) {
            if (address.length() > 0) address.append("+");
            address.append(state.trim());
        }

        return address.toString();
    }

    public String getMedicalFacilityAddress() {
        return buildMedicalFacilityAddress();
    }

    public void validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("Google Maps API key is not configured. Please check SPRING_GOOGLE_MAPS_API_KEY environment variable.");
        }
        log.info("Google Maps API key is configured (length: {})", apiKey.length());
    }

    private String removeVietnameseDiacritics(String text) {
        if (text == null) return null;

        return text
                // A variations
                .replace("à", "a").replace("á", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
                .replace("ă", "a").replace("ằ", "a").replace("ắ", "a").replace("ẳ", "a").replace("ẵ", "a").replace("ặ", "a")
                .replace("â", "a").replace("ầ", "a").replace("ấ", "a").replace("ẩ", "a").replace("ẫ", "a").replace("ậ", "a")
                .replace("À", "A").replace("Á", "A").replace("Ả", "A").replace("Ã", "A").replace("Ạ", "A")
                .replace("Ă", "A").replace("Ằ", "A").replace("Ắ", "A").replace("Ẳ", "A").replace("Ẵ", "A").replace("Ặ", "A")
                .replace("Â", "A").replace("Ầ", "A").replace("Ấ", "A").replace("Ẩ", "A").replace("Ẫ", "A").replace("Ậ", "A")
                // E variations
                .replace("è", "e").replace("é", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
                .replace("ê", "e").replace("ề", "e").replace("ế", "e").replace("ể", "e").replace("ễ", "e").replace("ệ", "e")
                .replace("È", "E").replace("É", "E").replace("Ẻ", "E").replace("Ẽ", "E").replace("Ẹ", "E")
                .replace("Ê", "E").replace("Ề", "E").replace("Ế", "E").replace("Ể", "E").replace("Ễ", "E").replace("Ệ", "E")
                // I variations
                .replace("ì", "i").replace("í", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i")
                .replace("Ì", "I").replace("Í", "I").replace("Ỉ", "I").replace("Ĩ", "I").replace("Ị", "I")
                // O variations
                .replace("ò", "o").replace("ó", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
                .replace("ô", "o").replace("ồ", "o").replace("ố", "o").replace("ổ", "o").replace("ỗ", "o").replace("ộ", "o")
                .replace("ơ", "o").replace("ờ", "o").replace("ớ", "o").replace("ở", "o").replace("ỡ", "o").replace("ợ", "o")
                .replace("Ò", "O").replace("Ó", "O").replace("Ỏ", "O").replace("Õ", "O").replace("Ọ", "O")
                .replace("Ô", "O").replace("Ồ", "O").replace("Ố", "O").replace("Ổ", "O").replace("Ỗ", "O").replace("Ộ", "O")
                .replace("Ơ", "O").replace("Ờ", "O").replace("Ớ", "O").replace("Ở", "O").replace("Ỡ", "O").replace("Ợ", "O")
                // U variations
                .replace("ù", "u").replace("ú", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
                .replace("ư", "u").replace("ừ", "u").replace("ứ", "u").replace("ử", "u").replace("ữ", "u").replace("ự", "u")
                .replace("Ù", "U").replace("Ú", "U").replace("Ủ", "U").replace("Ũ", "U").replace("Ụ", "U")
                .replace("Ư", "U").replace("Ừ", "U").replace("Ứ", "U").replace("Ử", "U").replace("Ữ", "U").replace("Ự", "U")
                // Y variations
                .replace("ỳ", "y").replace("ý", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y")
                .replace("Ỳ", "Y").replace("Ý", "Y").replace("Ỷ", "Y").replace("Ỹ", "Y").replace("Ỵ", "Y")
                // D variations
                .replace("đ", "d").replace("Đ", "D");
    }
}