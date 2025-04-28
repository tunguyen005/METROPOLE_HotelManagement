package admin_dashboard.model;

import java.sql.Timestamp;

public class ModelRoom {
    private int id;
    private int floor;
    private String roomNum;
    private String roomType;
    private String status;
    private String bedSize;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ModelRoom(int id, int floor, String roomNum, String roomType, String status, String bedSize, String description, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.floor = floor;
        this.roomNum = roomNum;
        this.roomType = roomType;
        this.status = status;
        this.bedSize = bedSize;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getId() { return id; }
    public int getFloor() { return floor; }
    public String getRoomNum() { return roomNum; }
    public String getRoomType() { return roomType; }
    public String getStatus() { return status; }
    public String getBedSize() { return bedSize; }
    public String getDescription() { return description; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
}
