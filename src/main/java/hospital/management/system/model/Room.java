package hospital.management.system.model;

import java.math.BigDecimal;

public class Room {
    private int roomId;
    private String roomNumber;
    private RoomType roomType;
    private BigDecimal pricePerDay;
    private boolean available;

    public Room() {}

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public String getRoomTypeName() {
        return roomType != null ? roomType.name() : null;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = RoomType.fromString(roomType);
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
