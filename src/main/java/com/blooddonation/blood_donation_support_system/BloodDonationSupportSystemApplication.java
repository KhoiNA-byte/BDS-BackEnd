package com.blooddonation.blood_donation_support_system;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class BloodDonationSupportSystemApplication {

	public static void main(String[] args) {

		// Load .env
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// Inject env values into system properties so Spring can use ${...}
		Map<String, String> requiredKeys = new HashMap<>();
		dotenv.entries().forEach(entry -> requiredKeys.put(entry.getKey(), entry.getValue()));
		requiredKeys.forEach((key, value) -> {
			if (System.getProperty(key) == null) {
				System.setProperty(key, value);
			}
		});

		SpringApplication.run(BloodDonationSupportSystemApplication.class, args);
	}
}
