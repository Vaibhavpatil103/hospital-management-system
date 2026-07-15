package hospital.management.system.dao;

import hospital.management.system.config.DatabaseManager;
import hospital.management.system.model.DashboardMetricsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {
    private static final Logger logger = LoggerFactory.getLogger(DashboardDAO.class);

    public DashboardMetricsDTO fetchMetrics() {
        DashboardMetricsDTO metrics = new DashboardMetricsDTO();
        
        try (Connection conn = DatabaseManager.getConnection()) {
            // Patient stats
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM patients")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) metrics.setTotalPatients(rs.getInt(1));
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM patients WHERE status = 'ADMITTED'")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) metrics.setActivePatients(rs.getInt(1));
            }
            
            // Room stats
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM rooms WHERE is_available = TRUE")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) metrics.setAvailableRooms(rs.getInt(1));
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM rooms WHERE is_available = FALSE")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) metrics.setOccupiedRooms(rs.getInt(1));
            }

            // Employee stats
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM employees")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) metrics.setEmployeeCount(rs.getInt(1));
            }

            // Ambulance stats
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ambulances WHERE is_available = TRUE")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) metrics.setAmbulancesAvailable(rs.getInt(1));
            }

            // Revenue Today
            try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(total_amount) FROM bills WHERE DATE(bill_date) = CURDATE()")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    BigDecimal amt = rs.getBigDecimal(1);
                    metrics.setTodayRevenue(amt != null ? amt : BigDecimal.ZERO);
                }
            }

            // Notifications
            try (PreparedStatement ps = conn.prepareStatement("SELECT message, type, created_at FROM notifications WHERE is_read = FALSE ORDER BY created_at DESC LIMIT 5")) {
                ResultSet rs = ps.executeQuery();
                List<String> notifs = new ArrayList<>();
                while (rs.next()) {
                    notifs.add(rs.getString("type") + "|" + rs.getString("message") + "|" + rs.getTimestamp("created_at").getTime());
                }
                metrics.setNotifications(notifs);
            }

            // Recent Activity
            try (PreparedStatement ps = conn.prepareStatement("SELECT DATE_FORMAT(created_at, '%h:%i %p') as tm, action FROM audit_logs ORDER BY created_at DESC LIMIT 5")) {
                ResultSet rs = ps.executeQuery();
                List<String> acts = new ArrayList<>();
                while (rs.next()) {
                    acts.add(rs.getString("tm") + " - " + rs.getString("action"));
                }
                metrics.setRecentActivities(acts);
            }

            // Room Occupancy by Type
            try (PreparedStatement ps = conn.prepareStatement("SELECT room_type, COUNT(*) as count FROM rooms WHERE is_available = FALSE GROUP BY room_type")) {
                ResultSet rs = ps.executeQuery();
                Map<String, Integer> occStats = new HashMap<>();
                while (rs.next()) {
                    occStats.put(rs.getString("room_type"), rs.getInt("count"));
                }
                metrics.setRoomOccupancyStats(occStats);
            }

            // Note: Weekly Revenue Stats are now fetched separately via fetchRevenueByDateRange

        } catch (SQLException e) {
            logger.error("Error fetching dashboard metrics", e);
            throw new DataAccessException("Failed to load dashboard metrics", e);
        }
        
        return metrics;
    }

    public Map<String, BigDecimal> fetchRevenueByDateRange(int days) {
        Map<String, BigDecimal> weeklyRev = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT DATE(bill_date) as dt, SUM(total_amount) as total " +
                "FROM bills " +
                "WHERE bill_date >= DATE(NOW()) - INTERVAL ? DAY " +
                "GROUP BY DATE(bill_date) ORDER BY dt ASC")) {
            
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BigDecimal amt = rs.getBigDecimal("total");
                weeklyRev.put(rs.getString("dt"), amt != null ? amt : BigDecimal.ZERO);
            }
        } catch (SQLException e) {
            logger.error("Error fetching revenue stats", e);
        }
        return weeklyRev;
    }
}
