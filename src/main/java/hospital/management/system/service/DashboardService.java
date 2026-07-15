package hospital.management.system.service;

import hospital.management.system.dao.DashboardDAO;
import hospital.management.system.model.DashboardMetricsDTO;

public class DashboardService {
    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAO();
    }

    public DashboardMetricsDTO loadMetrics() {
        return dashboardDAO.fetchMetrics();
    }

    public java.util.Map<String, java.math.BigDecimal> loadRevenueStats(int days) {
        return dashboardDAO.fetchRevenueByDateRange(days);
    }
}
