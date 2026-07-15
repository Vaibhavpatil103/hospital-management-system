package hospital.management.system.service;

import hospital.management.system.dao.InventoryDAO;
import hospital.management.system.model.InventoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryDAO inventoryDAO;

    public InventoryService() {
        this.inventoryDAO = new InventoryDAO();
    }

    public List<InventoryItem> getAllInventory() {
        return inventoryDAO.findAll();
    }

    public void addInventoryItem(InventoryItem item) {
        if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required.");
        }
        inventoryDAO.save(item);
        logger.info("Added inventory item: {}", item.getItemName());
    }

    public void updateInventoryItem(InventoryItem item) {
        inventoryDAO.update(item);
        logger.info("Updated inventory item ID: {}", item.getItemId());
    }
}
