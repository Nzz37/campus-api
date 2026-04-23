package com.github.nzz37.campus.api;

public class Sensor {
    private String id;
    private String type; // e.g., "Temperature", "CO2"
    private String status; // e.g., "ACTIVE", "OFFLINE"
    private double currentValue;
    private String roomId;

    // Empty constructor required by JAX-RS
    public Sensor() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}