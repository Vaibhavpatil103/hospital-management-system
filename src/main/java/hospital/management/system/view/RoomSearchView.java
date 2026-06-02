package hospital.management.system.view;

import hospital.management.system.model.Room;
import hospital.management.system.service.RoomService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomSearchView extends BaseFrame {

    private final RoomService roomService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;

    public RoomSearchView() {
        super("Search Available Rooms", 800, 600);
        this.roomService = new RoomService();
        setupUI();
        loadData(null);
        setVisible(true);
    }

    private void setupUI() {
        contentPanel.setLayout(new BorderLayout(0, 15));

        // Filter Panel
        JPanel filterPanel = UIComponentFactory.createCardPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        filterPanel.add(UIComponentFactory.createLabel("Room Type:"));
        typeCombo = UIComponentFactory.createComboBox(new String[]{"All", "General", "Private", "ICU"});
        filterPanel.add(typeCombo);

        JButton searchBtn = UIComponentFactory.createPrimaryButton("Search", e -> {
            String type = (String) typeCombo.getSelectedItem();
            loadData("All".equals(type) ? null : type);
        });
        filterPanel.add(searchBtn);

        contentPanel.add(filterPanel, BorderLayout.NORTH);

        // Table
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

    private void loadData(String roomType) {
        SwingWorker<List<Room>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Room> doInBackground() {
                if (roomType == null) {
                    return roomService.getAvailableRooms();
                } else {
                    return roomService.getAvailableRoomsByType(roomType);
                }
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
                                "Yes"
                        });
                    }
                } catch (Exception e) {
                    showError("Failed to search rooms: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
