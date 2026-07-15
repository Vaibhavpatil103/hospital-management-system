package hospital.management.system.view;

import hospital.management.system.model.Room;
import hospital.management.system.service.RoomService;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomView extends JPanel {

    private final RoomService roomService;
    private JTable table;
    private DefaultTableModel tableModel;

    public RoomView() {
        this.roomService = new RoomService();
        setupUI();
        loadData();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        // Top Panel
        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = UIComponentFactory.createPrimaryButton("Refresh", e -> loadData());
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

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
        add(scrollPane, BorderLayout.CENTER);
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
                    JOptionPane.showMessageDialog(RoomView.this, "Failed to load rooms: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
