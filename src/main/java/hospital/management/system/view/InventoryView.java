package hospital.management.system.view;

import hospital.management.system.model.InventoryItem;
import hospital.management.system.service.InventoryService;
import hospital.management.system.util.AppTheme;
import hospital.management.system.util.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class InventoryView extends JPanel {
    private final InventoryService inventoryService;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<InventoryItem> currentInventory;

    public InventoryView() {
        this.inventoryService = new InventoryService();
        setupUI();
        loadData();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        JPanel topPanel = UIComponentFactory.createPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton refreshBtn = UIComponentFactory.createSecondaryButton("Refresh", e -> loadData());
        JButton addBtn = UIComponentFactory.createSuccessButton("Add Item", e -> showInventoryDialog(null));
        JButton updateBtn = UIComponentFactory.createPrimaryButton("Update Selected", e -> updateSelectedItem());

        topPanel.add(refreshBtn);
        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Item Name", "Category", "Quantity", "Unit Price", "Last Updated"};
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
        SwingWorker<List<InventoryItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<InventoryItem> doInBackground() {
                return inventoryService.getAllInventory();
            }

            @Override
            protected void done() {
                try {
                    currentInventory = get();
                    tableModel.setRowCount(0);
                    for (InventoryItem item : currentInventory) {
                        tableModel.addRow(new Object[]{
                            item.getItemId(), item.getItemName(), item.getCategory(),
                            item.getQuantity(), "$" + item.getUnitPrice(), item.getUpdatedAt()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(InventoryView.this, "Failed to load inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showInventoryDialog(InventoryItem item) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), item == null ? "Add Inventory Item" : "Update Inventory Item", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = UIComponentFactory.createTextField();
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"MEDICINE", "CONSUMABLE", "EQUIPMENT"});
        JTextField qtyField = UIComponentFactory.createTextField();
        JTextField priceField = UIComponentFactory.createTextField();

        if (item != null) {
            nameField.setText(item.getItemName());
            categoryBox.setSelectedItem(item.getCategory());
            qtyField.setText(String.valueOf(item.getQuantity()));
            priceField.setText(item.getUnitPrice().toString());
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(UIComponentFactory.createLabel("Item Name:"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Category:"), gbc);
        gbc.gridx = 1; panel.add(categoryBox, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Quantity:"), gbc);
        gbc.gridx = 1; panel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; panel.add(UIComponentFactory.createLabel("Unit Price:"), gbc);
        gbc.gridx = 1; panel.add(priceField, gbc);

        JButton saveBtn = UIComponentFactory.createPrimaryButton("Save", e -> {
            InventoryItem invItem = item != null ? item : new InventoryItem();
            invItem.setItemName(nameField.getText().trim());
            invItem.setCategory((String) categoryBox.getSelectedItem());
            try {
                invItem.setQuantity(Integer.parseInt(qtyField.getText().trim()));
                invItem.setUnitPrice(new BigDecimal(priceField.getText().trim()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid quantity or price");
                return;
            }

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    if (item == null) {
                        inventoryService.addInventoryItem(invItem);
                    } else {
                        inventoryService.updateInventoryItem(invItem);
                    }
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(InventoryView.this, "Failed to save item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(AppTheme.BACKGROUND);
        btnPanel.add(saveBtn);
        btnPanel.add(UIComponentFactory.createSecondaryButton("Cancel", e -> dialog.dispose()));
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateSelectedItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int itemId = (int) tableModel.getValueAt(row, 0);
        InventoryItem item = currentInventory.stream().filter(i -> i.getItemId() == itemId).findFirst().orElse(null);
        if (item != null) {
            showInventoryDialog(item);
        }
    }
}
