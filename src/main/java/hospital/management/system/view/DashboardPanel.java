package hospital.management.system.view;

import hospital.management.system.dao.AppointmentDAO;
import hospital.management.system.dao.DoctorDAO;
import hospital.management.system.model.Appointment;
import hospital.management.system.model.DashboardMetricsDTO;
import hospital.management.system.model.Doctor;
import hospital.management.system.service.DashboardService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.view.components.StatCard;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private final DashboardService dashboardService;
    private final AppointmentDAO appointmentDAO;
    private final DoctorDAO doctorDAO;

    private JPanel kpiPanel;
    private JPanel chartPanel;
    private JPanel sidePanel;
    private JPanel widgetsPanel;
    
    private JComboBox<String> dateFilterCombo;
    private int currentRevenueDays = 7;

    public DashboardPanel() {
        this.dashboardService = new DashboardService();
        this.appointmentDAO = new AppointmentDAO();
        this.doctorDAO = new DoctorDAO();
        
        setLayout(new MigLayout("fill, insets 20", "[grow 70][grow 30]", "[][grow][]"));
        setBackground(AppTheme.BACKGROUND);

        initUI();
        loadDataAsync();
    }

    private void initUI() {
        // KPI Panel
        kpiPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow][grow][grow]", "[]"));
        kpiPanel.setOpaque(false);
        add(kpiPanel, "span 2, growx, wrap, gapbottom 20");

        // Show Skeleton KPIs
        for (int i = 0; i < 4; i++) {
            kpiPanel.add(new StatCard("Loading...", "██████", "/icon/search-icon.png"), "grow");
        }

        // Chart Panel with controls
        JPanel chartContainer = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        chartContainer.setOpaque(false);
        
        JPanel chartHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        chartHeader.setOpaque(false);
        dateFilterCombo = new JComboBox<>(new String[]{"Last 7 Days", "Last 30 Days"});
        dateFilterCombo.addActionListener(e -> {
            currentRevenueDays = dateFilterCombo.getSelectedIndex() == 0 ? 7 : 30;
            loadDataAsync();
        });
        chartHeader.add(new JLabel("Revenue Interval:"));
        chartHeader.add(dateFilterCombo);
        chartContainer.add(chartHeader, "growx, wrap");

        chartPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow]", "[grow]"));
        chartPanel.setOpaque(false);
        chartPanel.add(createSkeletonPanel("Revenue"), "grow, gapright 10");
        chartPanel.add(createSkeletonPanel("Room Occupancy"), "grow, gapleft 10");
        chartContainer.add(chartPanel, "grow");
        
        add(chartContainer, "grow, gapright 20");

        // Side Panel (Notifications & Actions)
        sidePanel = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]20[]20[]"));
        sidePanel.setOpaque(false);
        sidePanel.add(createSkeletonPanel("Quick Actions"), "growx, wrap");
        sidePanel.add(createSkeletonPanel("Notifications"), "growx, wrap");
        sidePanel.add(createSkeletonPanel("Recent Activity"), "growx");

        add(sidePanel, "growx, aligny top, wrap");
        
        // Bottom Widgets Panel
        widgetsPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow]", "[grow]"));
        widgetsPanel.setOpaque(false);
        widgetsPanel.add(createSkeletonPanel("Today's Appointments"), "grow, gapright 10");
        widgetsPanel.add(createSkeletonPanel("Available Doctors"), "grow, gapleft 10");
        add(widgetsPanel, "span 2, grow, gaptop 20");
    }

    private JPanel createSkeletonPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        
        JLabel titleLbl = new JLabel("  " + title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(AppTheme.TEXT_SECONDARY);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(titleLbl, BorderLayout.NORTH);
        
        JLabel skeletonLbl = new JLabel("Loading...", SwingConstants.CENTER);
        skeletonLbl.setForeground(new Color(220, 220, 220));
        panel.add(skeletonLbl, BorderLayout.CENTER);
        
        return panel;
    }

    private void loadDataAsync() {
        SwingWorker<DashboardMetricsDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardMetricsDTO doInBackground() throws Exception {
                DashboardMetricsDTO metrics = dashboardService.loadMetrics();
                metrics.setWeeklyRevenueStats(dashboardService.loadRevenueStats(currentRevenueDays));
                return metrics;
            }

            @Override
            protected void done() {
                try {
                    DashboardMetricsDTO metrics = get();
                    updateUIWithMetrics(metrics);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(DashboardPanel.this, "Failed to load dashboard data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateUIWithMetrics(DashboardMetricsDTO metrics) {
        // Update KPIs
        kpiPanel.removeAll();
        kpiPanel.add(new StatCard("Total Patients", String.valueOf(metrics.getTotalPatients()), "/icon/patients-icon.png"), "grow");
        
        int totalRooms = metrics.getAvailableRooms() + metrics.getOccupiedRooms();
        int activePct = totalRooms > 0 ? (metrics.getOccupiedRooms() * 100) / totalRooms : 0;
        kpiPanel.add(new StatCard("Active Rooms", activePct + "%", "/icon/room-icon.png"), "grow");
        
        kpiPanel.add(new StatCard("Revenue Today", "Rs " + metrics.getTodayRevenue(), "/icon/discharge-icon.png"), "grow");
        kpiPanel.add(new StatCard("Ambulances", metrics.getAmbulancesAvailable() + " Available", "/icon/ambulance-icon.png"), "grow");
        
        kpiPanel.revalidate();
        kpiPanel.repaint();

        // Update Charts
        chartPanel.removeAll();
        chartPanel.add(createRevenueChart(metrics.getWeeklyRevenueStats()), "grow, gapright 10");
        chartPanel.add(createOccupancyChart(metrics.getRoomOccupancyStats()), "grow, gapleft 10");
        chartPanel.revalidate();
        chartPanel.repaint();

        // Update Side Panel
        sidePanel.removeAll();
        
        // Quick Actions
        JPanel actions = new JPanel(new MigLayout("fillx, insets 15", "[grow]", "[]10[]10[]"));
        actions.setBackground(AppTheme.CARD_BACKGROUND);
        actions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        
        JButton btnAddPatient = new JButton("+ Register Patient");
        btnAddPatient.addActionListener(e -> new PatientRegistrationView());
        
        JButton btnAdmit = new JButton("+ Admit Patient");
        btnAdmit.addActionListener(e -> new PatientUpdateView()); 
        
        actions.add(btnAddPatient, "growx, wrap");
        actions.add(btnAdmit, "growx, wrap");
        sidePanel.add(actions, "growx, wrap");

        // Notifications
        JPanel notifs = new JPanel(new MigLayout("fillx, insets 15", "[grow]", "[]5[]"));
        notifs.setBackground(AppTheme.CARD_BACKGROUND);
        notifs.setBorder(BorderFactory.createTitledBorder("Notifications"));
        long now = System.currentTimeMillis();
        for (String n : metrics.getNotifications()) {
            String[] parts = n.split("\\|");
            String type = parts[0];
            String msg = parts[1];
            long time = Long.parseLong(parts[2]);
            
            long minsAgo = (now - time) / 60000;
            String timeStr = minsAgo == 0 ? "Just now" : minsAgo + " mins ago";
            if (minsAgo > 60) {
                long hrsAgo = minsAgo / 60;
                timeStr = hrsAgo + " hours ago";
            }
            
            JLabel lbl = new JLabel("<html><b>" + type + "</b>: " + msg + " <br/><small><font color='#888888'>" + timeStr + "</font></small></html>");
            if (type.equals("CRITICAL")) lbl.setForeground(new Color(220, 53, 69));
            else if (type.equals("WARNING")) lbl.setForeground(new Color(253, 126, 20));
            else if (type.equals("INFO")) lbl.setForeground(new Color(13, 110, 253));
            
            notifs.add(lbl, "wrap");
        }
        if(metrics.getNotifications().isEmpty()) notifs.add(new JLabel("No new notifications"));
        sidePanel.add(notifs, "growx, wrap");

        // Activity
        JPanel activity = new JPanel(new MigLayout("fillx, insets 15", "[grow]", "[]5[]"));
        activity.setBackground(AppTheme.CARD_BACKGROUND);
        activity.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        for (String a : metrics.getRecentActivities()) {
            activity.add(new JLabel("• " + a), "wrap");
        }
        if(metrics.getRecentActivities().isEmpty()) activity.add(new JLabel("No recent activity"));
        sidePanel.add(activity, "growx");

        sidePanel.revalidate();
        sidePanel.repaint();
        
        // Update Bottom Widgets
        widgetsPanel.removeAll();
        
        // Today's Appointments
        JPanel apptWidget = new JPanel(new BorderLayout());
        apptWidget.setBackground(AppTheme.CARD_BACKGROUND);
        apptWidget.setBorder(BorderFactory.createTitledBorder("Today's Appointments"));
        List<Appointment> todayAppts = appointmentDAO.findTodayAppointments();
        
        String[] apptCols = {"Time", "Patient", "Phone"};
        String[][] apptData = new String[todayAppts.size()][3];
        for (int i = 0; i < todayAppts.size(); i++) {
            Appointment a = todayAppts.get(i);
            apptData[i][0] = a.getAppointmentTime().toString();
            apptData[i][1] = a.getPatientName();
            apptData[i][2] = a.getPatientPhone();
        }
        JTable apptTable = new JTable(apptData, apptCols);
        apptTable.setEnabled(false);
        apptWidget.add(new JScrollPane(apptTable), BorderLayout.CENTER);
        if(todayAppts.isEmpty()) apptWidget.add(new JLabel("No appointments scheduled for today", SwingConstants.CENTER), BorderLayout.CENTER);
        widgetsPanel.add(apptWidget, "grow, gapright 10");
        
        // Available Doctors
        JPanel docWidget = new JPanel(new BorderLayout());
        docWidget.setBackground(AppTheme.CARD_BACKGROUND);
        docWidget.setBorder(BorderFactory.createTitledBorder("Available Doctors"));
        List<Doctor> activeDocs = doctorDAO.findActiveDoctors();
        
        String[] docCols = {"Name", "Specialization", "Contact"};
        String[][] docData = new String[activeDocs.size()][3];
        for (int i = 0; i < activeDocs.size(); i++) {
            Doctor d = activeDocs.get(i);
            docData[i][0] = d.getFullName();
            docData[i][1] = d.getSpecialization();
            docData[i][2] = d.getPhone() != null ? d.getPhone() : "N/A";
        }
        JTable docTable = new JTable(docData, docCols);
        docTable.setEnabled(false);
        docWidget.add(new JScrollPane(docTable), BorderLayout.CENTER);
        if(activeDocs.isEmpty()) docWidget.add(new JLabel("No active doctors found", SwingConstants.CENTER), BorderLayout.CENTER);
        widgetsPanel.add(docWidget, "grow, gapleft 10");
        
        widgetsPanel.revalidate();
        widgetsPanel.repaint();
    }

    private ChartPanel createRevenueChart(Map<String, BigDecimal> data) {
        TimeSeries series = new TimeSeries("Revenue");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Pre-fill days to ensure chart scales properly and shows empty days
        LocalDate today = LocalDate.now();
        for (int i = currentRevenueDays - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(dtf);
            BigDecimal val = data.getOrDefault(dateStr, BigDecimal.ZERO);
            
            // JFreeChart Day uses java.util.Date
            Calendar cal = Calendar.getInstance();
            cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
            series.addOrUpdate(new Day(cal.getTime()), val);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Revenue (Last " + currentRevenueDays + " Days)", "", "Amount", dataset, false, true, false);
        
        chart.setBackgroundPaint(AppTheme.CARD_BACKGROUND);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(AppTheme.CARD_BACKGROUND);
        plot.setDomainGridlinePaint(AppTheme.BORDER_LIGHT);
        plot.setRangeGridlinePaint(AppTheme.BORDER_LIGHT);
        
        // Format Date Axis (fixes the weird timestamp issue)
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        if (currentRevenueDays <= 7) {
            axis.setDateFormatOverride(new SimpleDateFormat("EEE")); // Mon, Tue
        } else {
            axis.setDateFormatOverride(new SimpleDateFormat("MMM dd")); // Oct 12
        }
        
        XYAreaRenderer renderer = new XYAreaRenderer();
        renderer.setSeriesPaint(0, new Color(0, 150, 255, 100)); // Semi-transparent blue
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    private ChartPanel createOccupancyChart(Map<String, Integer> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (entry.getValue() > 0) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        JFreeChart chart = ChartFactory.createRingChart(
                "Room Occupancy", dataset, true, true, false);
        
        chart.setBackgroundPaint(AppTheme.CARD_BACKGROUND);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(AppTheme.CARD_BACKGROUND);
        plot.setSectionPaint("GENERAL", new Color(76, 175, 80));
        plot.setSectionPaint("PRIVATE", new Color(33, 150, 243));
        plot.setSectionPaint("ICU", new Color(244, 67, 54));
        
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(null); // Hide ugly connecting lines
        
        // Fallback for empty data
        plot.setNoDataMessage("No rooms currently occupied");

        return new ChartPanel(chart);
    }
}
