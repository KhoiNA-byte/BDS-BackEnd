package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.DashboardDto;
import com.blooddonation.blood_donation_support_system.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get complete dashboard data for staff
     * @return ResponseEntity containing DashboardDto with all dashboard statistics
     */
    @GetMapping("/staff")
    public ResponseEntity<Object> getStaffDashboard() {
        try {
            DashboardDto dashboardData = dashboardService.getDashboardData();
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get blood request statistics only
     * @return ResponseEntity containing BloodRequestStatsDto
     */
    @GetMapping("/blood-requests/stats")
    public ResponseEntity<Object> getBloodRequestStats() {
        try {
            DashboardDto.BloodRequestStatsDto stats = dashboardService.getBloodRequestStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get blog statistics only
     * @return ResponseEntity containing BlogStatsDto
     */
    @GetMapping("/blogs/stats")
    public ResponseEntity<Object> getBlogStats() {
        try {
            DashboardDto.BlogStatsDto stats = dashboardService.getBlogStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get donation event statistics only
     * @return ResponseEntity containing DonationEventStatsDto
     */
    @GetMapping("/donation-events/stats")
    public ResponseEntity<Object> getDonationEventStats() {
        try {
            DashboardDto.DonationEventStatsDto stats = dashboardService.getDonationEventStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get blood stock information
     * @return ResponseEntity containing List of BloodStockDto
     */
    @GetMapping("/blood-stock")
    public ResponseEntity<Object> getBloodStock() {
        try {
            List<DashboardDto.BloodStockDto> bloodStock = dashboardService.getBloodStock();
            return ResponseEntity.ok(bloodStock);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get donation event chart data for specified timeframe
     * @param timeframe The timeframe for chart data (week, month, year)
     * @return ResponseEntity containing List of DonationEventChartDto
     */
    @GetMapping("/donation-events/chart")
    public ResponseEntity<Object> getDonationEventChartData(
            @RequestParam(defaultValue = "week") String timeframe) {
        try {
            List<DashboardDto.DonationEventChartDto> chartData = 
                dashboardService.getDonationEventChartData(timeframe);
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Admin Dashboard Endpoints
    /**
     * Get complete admin dashboard data
     * @return ResponseEntity containing AdminDashboardDto with all admin dashboard statistics
     */
    @GetMapping("/admin")
    public ResponseEntity<Object> getAdminDashboard() {
        try {
            DashboardDto.AdminDashboardDto adminDashboardData = dashboardService.getAdminDashboardData();
            return ResponseEntity.ok(adminDashboardData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get total stats for admin dashboard
     * @return ResponseEntity containing TotalStatsDto
     */
    @GetMapping("/admin/total-stats")
    public ResponseEntity<Object> getAdminTotalStats() {
        try {
            DashboardDto.TotalStatsDto stats = dashboardService.getTotalStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get recent activities for admin dashboard
     * @return ResponseEntity containing list of RecentActivityDto
     */
    @GetMapping("/admin/recent-activities")
    public ResponseEntity<Object> getAdminRecentActivities() {
        try {
            List<DashboardDto.RecentActivityDto> activities = dashboardService.getRecentActivities();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
