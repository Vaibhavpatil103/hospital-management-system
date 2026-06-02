package hospital.management.system.view;

import hospital.management.system.dao.DepartmentDAO;
import hospital.management.system.model.Department;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentView extends BaseFrame {

    private final DepartmentDAO departmentDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    public DepartmentView() {
        super("Department Information", 800, 500);
        this.departmentDAO = new DepartmentDAO();
        
        setupUI();
        loadData();
        setVisible(true);
    }

    private void setupUI() {
        contentPanel.setLayout(new BorderLayout(0, 10));

        // Top Panel
        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = UIComponentFactory.createPrimaryButton("Refresh", e -> loadData());
        topPanel.add(refreshBtn);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Department ID", "Department Name", "Head Doctor", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = UIComponentFactory.createTableScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer buttons
        JPanel buttonPanel = UIComponentFactory.createButtonPanel();
        JButton backBtn = UIComponentFactory.createDangerButton("Back", e -> dispose());
        buttonPanel.add(backBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        SwingWorker<List<Department>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Department> doInBackground() {
                return departmentDAO.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<Department> depts = get();
                    tableModel.setRowCount(0);
                    for (Department d : depts) {
                        tableModel.addRow(new Object[]{
                            d.getDeptId(), d.getDeptName(), d.getHeadDoctor(), d.getPhone()
                        });
                    }
                } catch (Exception e) {
                    showError("Failed to load departments: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
