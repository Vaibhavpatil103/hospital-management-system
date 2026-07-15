package hospital.management.system.view;

import hospital.management.system.model.AuditLog;
import hospital.management.system.service.AuditService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AuditLogView extends JPanel {
    private final AuditService auditService;
    private JTable table;
    private DefaultTableModel tableModel;

    public AuditLogView() {
        this.auditService = new AuditService();
        setupUI();
        loadData();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton refreshBtn = UIComponentFactory.createSecondaryButton("Refresh Logs", e -> loadData());
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Log ID", "Action", "Details", "User ID", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        JScrollPane scrollPane = UIComponentFactory.createTableScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        SwingWorker<List<AuditLog>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AuditLog> doInBackground() {
                return auditService.getAllLogs();
            }

            @Override
            protected void done() {
                try {
                    List<AuditLog> logs = get();
                    tableModel.setRowCount(0);
                    for (AuditLog log : logs) {
                        tableModel.addRow(new Object[]{
                            log.getLogId(),
                            log.getAction(),
                            log.getDetails(),
                            log.getUserId() != null ? log.getUserId() : "System",
                            log.getCreatedAt()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AuditLogView.this, "Failed to load audit logs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
