package hospital.management.system.view;

import hospital.management.system.model.Room;
import hospital.management.system.service.RoomService;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomView extends BaseFrame {

    private final RoomService roomService;
    private JTable table;
    private DefaultTableModel tableModel;

    public RoomView() {
        super("Room Information", 800, 600);
        this.roomService = new RoomService();
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

        // Table setup
        String[] columns = {"Room ID", "Room Number", "Type", "Price Per Day", "Available"};
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
        SwingWorker<List<Room>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Room> doInBackground() {
                return roomService.getAllRooms();
            }

            @Override
            protected void done() {
                try {
                    List<Room> rooms = get();
                    tableModel.setRowCount(0);
                    for (Room r : rooms) {
                        tableModel.addRow(new Object[]{
                                r.getRoomId(),
                                r.getRoomNumber(),
                                r.getRoomType(),
                                "$" + r.getPricePerDay(),
                                r.isAvailable() ? "Yes" : "No"
                        });
                    }
                } catch (Exception e) {
                    showError("Failed to load rooms: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
