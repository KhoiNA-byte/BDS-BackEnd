package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.DashboardDto;

import java.util.List;

public interface DashboardService {
    
    /**
     * Get complete dashboard data for staff
     * @return DashboardDto containing all dashboard statistics
     */
    DashboardDto getDashboardData();
    
    /**
     * Get blood request statistics
     * @return BloodRequestStatsDto with unfinished, fulfilled, and total counts
     */
    DashboardDto.BloodRequestStatsDto getBloodRequestStats();
    
    /**
     * Get blog statistics
     * @return BlogStatsDto with published, waiting, and total counts
     */
    DashboardDto.BlogStatsDto getBlogStats();
    
    /**
     * Get donation event statistics
     * @return DonationEventStatsDto with available, completed, and total counts
     */
    DashboardDto.DonationEventStatsDto getDonationEventStats();
    
    /**
     * Get blood stock information by blood type
     * @return List of BloodStockDto with stock information for each blood type
     */
    List<DashboardDto.BloodStockDto> getBloodStock();
    
    /**
     * Get donation event chart data for specified timeframe
     * @param timeframe The timeframe for chart data (week, month, year)
     * @return List of DonationEventChartDto with chart data points
     */
    List<DashboardDto.DonationEventChartDto> getDonationEventChartData(String timeframe);

    // Admin Dashboard Methods
    /**
     * Get complete admin dashboard data
     * @return AdminDashboardDto containing all admin dashboard statistics
     */
    DashboardDto.AdminDashboardDto getAdminDashboardData();

    /**
     * Get total stats for admin dashboard
     * @return TotalStatsDto with total donors, accounts, and monthly new counts
     */
    DashboardDto.TotalStatsDto getTotalStats();

    /**
     * Get recent activities for admin dashboard
     * @return List of RecentActivityDto with recent system activities
     */
    List<DashboardDto.RecentActivityDto> getRecentActivities();
}
