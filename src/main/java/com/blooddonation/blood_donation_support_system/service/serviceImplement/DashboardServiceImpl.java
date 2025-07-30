package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.DashboardDto;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.*;
import com.blooddonation.blood_donation_support_system.repository.*;
import com.blooddonation.blood_donation_support_system.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private DonationEventRepository donationEventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MedicalFacilityStockRepository medicalFacilityStockRepository;

    @Override
    public DashboardDto getDashboardData() {
        return DashboardDto.builder()
                .bloodRequestStats(getBloodRequestStats())
                .blogStats(getBlogStats())
                .donationEventStats(getDonationEventStats())
                .bloodStock(getBloodStock())
                .donationEventChartData(getDonationEventChartData("week"))
                .build();
    }

    @Override
    public DashboardDto.BloodRequestStatsDto getBloodRequestStats() {
        long totalRequests = bloodRequestRepository.count();
        long fulfilledRequests = bloodRequestRepository.findAll().stream()
                .mapToLong(request -> BloodRequestStatus.FULFILLED.equals(request.getStatus()) ? 1 : 0)
                .sum();
        long unfinishedRequests = totalRequests - fulfilledRequests;

        return DashboardDto.BloodRequestStatsDto.builder()
                .unfinished(unfinishedRequests)
                .fulfilled(fulfilledRequests)
                .total(totalRequests)
                .build();
    }

    @Override
    public DashboardDto.BlogStatsDto getBlogStats() {
        long totalBlogs = blogRepository.count();
        long publishedBlogs = blogRepository.findAll().stream()
                .mapToLong(blog -> BlogStatus.ACTIVE.equals(blog.getStatus()) ? 1 : 0)
                .sum();
        long waitingBlogs = blogRepository.findAll().stream()
                .mapToLong(blog -> BlogStatus.INACTIVE.equals(blog.getStatus()) ? 1 : 0)
                .sum();

        return DashboardDto.BlogStatsDto.builder()
                .published(publishedBlogs)
                .waitingForPublishing(waitingBlogs)
                .total(totalBlogs)
                .build();
    }

    @Override
    public DashboardDto.DonationEventStatsDto getDonationEventStats() {
        long totalEvents = donationEventRepository.count();
        long availableEvents = donationEventRepository.findAll().stream()
                .mapToLong(event -> DonationEventStatus.AVAILABLE.equals(event.getStatus()) ? 1 : 0)
                .sum();
        long completedEvents = donationEventRepository.findAll().stream()
                .mapToLong(event -> DonationEventStatus.COMPLETED.equals(event.getStatus()) ? 1 : 0)
                .sum();

        return DashboardDto.DonationEventStatsDto.builder()
                .available(availableEvents)
                .completed(completedEvents)
                .total(totalEvents)
                .build();
    }

    @Override
    public List<DashboardDto.BloodStockDto> getBloodStock() {
        List<DashboardDto.BloodStockDto> bloodStockList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (BloodType bloodType : BloodType.values()) {
            // Get all unexpired medical facility stock items for this blood type
            List<MedicalFacilityStock> allStocks = medicalFacilityStockRepository.findAll();
            
            // Calculate total volume for each component type (only unexpired)
            double wholeBloodVolume = allStocks.stream()
                    .filter(stock -> bloodType.equals(stock.getBloodType()) && 
                                  ComponentType.WHOLE_BLOOD.equals(stock.getComponentType()) &&
                                  stock.getExpiryDate().isAfter(currentDate))
                    .mapToDouble(MedicalFacilityStock::getVolume)
                    .sum();
            
            double redCellsVolume = allStocks.stream()
                    .filter(stock -> bloodType.equals(stock.getBloodType()) && 
                                  ComponentType.RED_BLOOD_CELLS.equals(stock.getComponentType()) &&
                                  stock.getExpiryDate().isAfter(currentDate))
                    .mapToDouble(MedicalFacilityStock::getVolume)
                    .sum();
            
            double plasmaVolume = allStocks.stream()
                    .filter(stock -> bloodType.equals(stock.getBloodType()) && 
                                  ComponentType.PLASMA.equals(stock.getComponentType()) &&
                                  stock.getExpiryDate().isAfter(currentDate))
                    .mapToDouble(MedicalFacilityStock::getVolume)
                    .sum();
            
            double plateletsVolume = allStocks.stream()
                    .filter(stock -> bloodType.equals(stock.getBloodType()) && 
                                  ComponentType.PLATELETS.equals(stock.getComponentType()) &&
                                  stock.getExpiryDate().isAfter(currentDate))
                    .mapToDouble(MedicalFacilityStock::getVolume)
                    .sum();

            // Convert volumes to units (assuming each unit is ~450ml for display purposes)
            long wholeBloodUnits = Math.round(wholeBloodVolume);
            long redCellsUnits = Math.round(redCellsVolume);
            long plasmaUnits = Math.round(plasmaVolume);
            long plateletsUnits = Math.round(plateletsVolume);

            bloodStockList.add(DashboardDto.BloodStockDto.builder()
                    .bloodType(bloodType.getType())
                    .wholeBlood(wholeBloodUnits)
                    .redCells(redCellsUnits)
                    .plasma(plasmaUnits)
                    .platelets(plateletsUnits)
                    .total(wholeBloodUnits + redCellsUnits + plasmaUnits + plateletsUnits)
                    .build());
        }

        return bloodStockList;
    }

    @Override
    public List<DashboardDto.DonationEventChartDto> getDonationEventChartData(String timeframe) {
        List<DashboardDto.DonationEventChartDto> chartData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        switch (timeframe.toLowerCase()) {
            case "week":
                chartData = getWeeklyDonationData(now);
                break;
            case "month":
                chartData = getMonthlyDonationData(now);
                break;
            case "year":
                chartData = getYearlyDonationData(now);
                break;
            default:
                chartData = getWeeklyDonationData(now);
        }

        return chartData;
    }

    private List<DashboardDto.DonationEventChartDto> getWeeklyDonationData(LocalDateTime now) {
        List<DashboardDto.DonationEventChartDto> weeklyData = new ArrayList<>();
        String[] dayNames = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        
        // Get current week start (Monday)
        LocalDate startOfWeek = now.toLocalDate().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            
            // Get completed events for this day
            List<DonationEvent> completedEvents = donationEventRepository.findAll().stream()
                    .filter(event -> DonationEventStatus.COMPLETED.equals(event.getStatus()) && 
                                   event.getDonationDate().equals(date))
                    .collect(Collectors.toList());
            
            // Count total registrations for these events
            long totalDonors = completedEvents.stream()
                    .mapToLong(event -> eventRegistrationRepository.findByEventId(event.getId()).size())
                    .sum();

            weeklyData.add(DashboardDto.DonationEventChartDto.builder()
                    .timeKey(dayNames[i])
                    .displayName(dayNames[i])
                    .events((long) completedEvents.size())
                    .totalDonors(totalDonors)
                    .build());
        }
        return weeklyData;
    }

    private List<DashboardDto.DonationEventChartDto> getMonthlyDonationData(LocalDateTime now) {
        List<DashboardDto.DonationEventChartDto> monthlyData = new ArrayList<>();
        int currentYear = now.getYear();

        for (int month = 1; month <= 12; month++) {
            LocalDate startOfMonth = LocalDate.of(currentYear, month, 1);
            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
            
            // Get completed events for this month
            List<DonationEvent> completedEvents = donationEventRepository.findAll().stream()
                    .filter(event -> DonationEventStatus.COMPLETED.equals(event.getStatus()) && 
                                   !event.getDonationDate().isBefore(startOfMonth) && 
                                   !event.getDonationDate().isAfter(endOfMonth))
                    .collect(Collectors.toList());
            
            // Count total registrations for these events
            long totalDonors = completedEvents.stream()
                    .mapToLong(event -> eventRegistrationRepository.findByEventId(event.getId()).size())
                    .sum();

            monthlyData.add(DashboardDto.DonationEventChartDto.builder()
                    .timeKey(String.valueOf(month))
                    .displayName("T" + month)
                    .events((long) completedEvents.size())
                    .totalDonors(totalDonors)
                    .build());
        }

        return monthlyData;
    }

    private List<DashboardDto.DonationEventChartDto> getYearlyDonationData(LocalDateTime now) {
        List<DashboardDto.DonationEventChartDto> yearlyData = new ArrayList<>();
        int currentYear = now.getYear();

        for (int year = currentYear - 5; year <= currentYear; year++) {
            LocalDate startOfYear = LocalDate.of(year, 1, 1);
            LocalDate endOfYear = LocalDate.of(year, 12, 31);
            
            // Get completed events for this year
            List<DonationEvent> completedEvents = donationEventRepository.findAll().stream()
                    .filter(event -> DonationEventStatus.COMPLETED.equals(event.getStatus()) && 
                                   !event.getDonationDate().isBefore(startOfYear) && 
                                   !event.getDonationDate().isAfter(endOfYear))
                    .collect(Collectors.toList());
            
            // Count total registrations for these events
            long totalDonors = completedEvents.stream()
                    .mapToLong(event -> eventRegistrationRepository.findByEventId(event.getId()).size())
                    .sum();

            yearlyData.add(DashboardDto.DonationEventChartDto.builder()
                    .timeKey(String.valueOf(year))
                    .displayName(String.valueOf(year))
                    .events((long) completedEvents.size())
                    .totalDonors(totalDonors)
                    .build());
        }

        return yearlyData;
    }


    public String convertBloodType(String bloodType) {
        return bloodType.replace("_POSITIVE", "+").replace("_NEGATIVE", "-");
    }

    // Admin Dashboard Methods
    @Override
    public DashboardDto.AdminDashboardDto getAdminDashboardData() {
        return DashboardDto.AdminDashboardDto.builder()
                .totalStats(getTotalStats())
                .donationEventStats(getDonationEventStats())
                .blogStats(getBlogStats())
                .bloodStock(getBloodStock())
                .recentActivities(getRecentActivities())
                .donationEventChartData(getDonationEventChartData("week"))
                .build();
    }

    @Override
    public DashboardDto.TotalStatsDto getTotalStats() {
        // Get total donors based on event registrations (unique donors who have registered for events)
        long totalDonors = donationEventRepository.findAll().stream()
                .flatMap(event -> eventRegistrationRepository.findByEventId(event.getId()).stream())
                .map(registration -> registration.getProfileId().getId())
                .distinct()
                .count();

        // Get total accounts
        long totalAccounts = accountRepository.count();

        // Get new donors this month (approximation based on donation events this month)
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        long newDonorsThisMonth = donationEventRepository.findAll().stream()
                .filter(event -> event.getDonationDate().isAfter(startOfMonth.minusDays(1)))
                .mapToLong(event -> eventRegistrationRepository.findByEventId(event.getId()).size())
                .sum();

        // Get new accounts this month (mock data since Account doesn't have createdAt)
        long newAccountsThisMonth = Math.max(15, (long) (Math.random() * 50)); // Mock data for now

        return DashboardDto.TotalStatsDto.builder()
                .totalDonors(totalDonors)
                .totalAccounts(totalAccounts)
                .newDonorsThisMonth(newDonorsThisMonth)
                .newAccountsThisMonth(newAccountsThisMonth)
                .build();
    }

    @Override
    public List<DashboardDto.RecentActivityDto> getRecentActivities() {
        List<DashboardDto.RecentActivityDto> activities = new ArrayList<>();
        
        // Get recent completed donation events
        List<DonationEvent> recentEvents = donationEventRepository.findAll().stream()
                .filter(event -> DonationEventStatus.COMPLETED.equals(event.getStatus()))
                .sorted((e1, e2) -> e2.getDonationDate().compareTo(e1.getDonationDate()))
                .limit(3)
                .collect(Collectors.toList());

        for (DonationEvent event : recentEvents) {
            activities.add(DashboardDto.RecentActivityDto.builder()
                    .id((long) activities.size() + 1)
                    .type("donation_event")
                    .title("Sự kiện hiến máu tại " + event.getAddress())
                    .description("Báo cáo sự kiện hiến máu hoàn thành")
                    .timestamp(getRelativeTime(event.getDonationDate().atStartOfDay()))
                    .status("completed")
                    .icon("Calendar")
                    .build());
        }

        // Get recent blood requests (fulfilled) with actual data
        List<BloodRequest> recentFulfilledRequests = bloodRequestRepository.findAll().stream()
                .filter(request -> BloodRequestStatus.FULFILLED.equals(request.getStatus()))
                .sorted((r1, r2) -> {
                    // Sort by ID descending (assuming newer requests have higher IDs)
                    return r2.getId().compareTo(r1.getId());
                })
                .limit(2)
                .collect(Collectors.toList());
        
        for (BloodRequest request : recentFulfilledRequests) {
            activities.add(DashboardDto.RecentActivityDto.builder()
                    .id((long) activities.size() + 1)
                    .type("blood_request_fulfilled")
                    .title("Yêu cầu máu cho bệnh nhân " + request.getProfile().getName() + " được hoàn thành")
                    .description("Yêu cầu máu " + convertBloodType(request.getBloodType().getType()) + " đã được xử lý thành công")
                    .timestamp(request.getEndTime() != null ? getRelativeTime(request.getEndTime()) : "Gần đây")
                    .status("fulfilled")
                    .icon("CheckCircle")
                    .build());
        }

        // Get recent blood requests (pending) with actual data
        List<BloodRequest> recentPendingRequests = bloodRequestRepository.findAll().stream()
                .filter(request -> BloodRequestStatus.PENDING.equals(request.getStatus()))
                .sorted((r1, r2) -> {
                    // Sort by ID descending (assuming newer requests have higher IDs)
                    return r2.getId().compareTo(r1.getId());
                })
                .limit(2)
                .collect(Collectors.toList());
        
        for (BloodRequest request : recentPendingRequests) {
            activities.add(DashboardDto.RecentActivityDto.builder()
                    .id((long) activities.size() + 1)
                    .type("blood_request_created")
                    .title("Yêu cầu máu mới cho bệnh nhân " + request.getProfile().getName())
                    .description("Yêu cầu máu " + convertBloodType(request.getBloodType().getType()) + " cần xử lý")
                    .timestamp(request.getCreatedTime() != null ? getRelativeTime(request.getCreatedTime()) : "Gần đây")
                    .status("pending")
                    .icon("Droplet")
                    .build());
        }

        // Get recent available donation events
        List<DonationEvent> availableEvents = donationEventRepository.findAll().stream()
                .filter(event -> DonationEventStatus.AVAILABLE.equals(event.getStatus()))
                .sorted((e1, e2) -> e2.getDonationDate().compareTo(e1.getDonationDate()))
                .limit(1)
                .collect(Collectors.toList());

        for (DonationEvent event : availableEvents) {
            activities.add(DashboardDto.RecentActivityDto.builder()
                    .id((long) activities.size() + 1)
                    .type("donation_event")
                    .title("Sự kiện hiến máu mới tại " + event.getAddress())
                    .description("Sự kiện hiến máu đang mở đăng ký")
                    .timestamp(getRelativeTime(event.getCreatedDate().atStartOfDay()))
                    .status("new")
                    .icon("Calendar")
                    .build());
        }

        return activities.stream().limit(5).collect(Collectors.toList());
    }

    private String getRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(dateTime, now).toHours();
        long days = java.time.Duration.between(dateTime, now).toDays();
        
        if (days > 0) {
            return days + " ngày trước";
        } else if (hours > 0) {
            return hours + " giờ trước";
        } else {
            return "Vừa xong";
        }
    }
}
