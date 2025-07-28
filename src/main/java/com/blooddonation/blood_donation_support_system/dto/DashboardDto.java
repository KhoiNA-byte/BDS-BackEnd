package com.blooddonation.blood_donation_support_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDto {
    private BloodRequestStatsDto bloodRequestStats;
    private BlogStatsDto blogStats;
    private DonationEventStatsDto donationEventStats;
    private List<BloodStockDto> bloodStock;
    private List<DonationEventChartDto> donationEventChartData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BloodRequestStatsDto {
        private Long unfinished;
        private Long fulfilled;
        private Long total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BlogStatsDto {
        private Long published;
        private Long waitingForPublishing;
        private Long total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DonationEventStatsDto {
        private Long available;
        private Long completed;
        private Long total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BloodStockDto {
        private String bloodType;
        private Long wholeBlood;
        private Long redCells;
        private Long plasma;
        private Long platelets;
        private Long total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DonationEventChartDto {
        private String timeKey;
        private String displayName;
        private Long events;
        private Long totalDonors;
    }

    // Admin Dashboard DTOs
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AdminDashboardDto {
        private TotalStatsDto totalStats;
        private DonationEventStatsDto donationEventStats;
        private BlogStatsDto blogStats;
        private List<BloodStockDto> bloodStock;
        private List<RecentActivityDto> recentActivities;
        private List<DonationEventChartDto> donationEventChartData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TotalStatsDto {
        private Long totalDonors;
        private Long totalAccounts;
        private Long newDonorsThisMonth;
        private Long newAccountsThisMonth;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RecentActivityDto {
        private Long id;
        private String type;
        private String title;
        private String description;
        private String timestamp;
        private String status;
        private String icon;
    }
}
