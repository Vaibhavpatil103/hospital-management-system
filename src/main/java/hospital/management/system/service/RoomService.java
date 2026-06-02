package hospital.management.system.service;

import hospital.management.system.dao.RoomDAO;
import hospital.management.system.model.Room;

import java.util.List;

public class RoomService {
    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
    }

    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomDAO.findAvailable();
    }

    public List<Room> getAvailableRoomsByType(String roomType) {
        return roomDAO.findAvailableByType(roomType);
    }
}
