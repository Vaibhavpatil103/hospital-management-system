package hospital.management.system.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardMetricsDTO {
    private int totalPatients;
    private int activePatients;
    private int availableRooms;
    private int occupiedRooms;
    private BigDecimal todayRevenue = BigDecimal.ZERO;
    private BigDecimal monthlyRevenue = BigDecimal.ZERO;
    private int employeeCount;
    private int ambulancesAvailable;

    private List<String> recentActivities = new ArrayList<>();
    private List<String> notifications = new ArrayList<>();
    
    // For Charts
    private Map<String, Integer> roomOccupancyStats;
    private Map<String, BigDecimal> weeklyRevenueStats;

    // Getters and Setters
    public int getTotalPatients() { return totalPatients; }
    public void setTotalPatients(int totalPatients) { this.totalPatients = totalPatients; }

    public int getActivePatients() { return activePatients; }
    public void setActivePatients(int activePatients) { this.activePatients = activePatients; }

    public int getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(int availableRooms) { this.availableRooms = availableRooms; }

    public int getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(int occupiedRooms) { this.occupiedRooms = occupiedRooms; }

    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }

    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

    public int getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(int employeeCount) { this.employeeCount = employeeCount; }

    public int getAmbulancesAvailable() { return ambulancesAvailable; }
    public void setAmbulancesAvailable(int ambulancesAvailable) { this.ambulancesAvailable = ambulancesAvailable; }

    public List<String> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<String> recentActivities) { this.recentActivities = recentActivities; }

    public List<String> getNotifications() { return notifications; }
    public void setNotifications(List<String> notifications) { this.notifications = notifications; }

    public Map<String, Integer> getRoomOccupancyStats() { return roomOccupancyStats; }
    public void setRoomOccupancyStats(Map<String, Integer> roomOccupancyStats) { this.roomOccupancyStats = roomOccupancyStats; }

    public Map<String, BigDecimal> getWeeklyRevenueStats() { return weeklyRevenueStats; }
    public void setWeeklyRevenueStats(Map<String, BigDecimal> weeklyRevenueStats) { this.weeklyRevenueStats = weeklyRevenueStats; }
}
