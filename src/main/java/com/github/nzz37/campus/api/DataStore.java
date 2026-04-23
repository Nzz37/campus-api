/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campus.api;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private static Map<String, Room> rooms = new HashMap<>();
    
    private static Map<String, Sensor> sensors = new HashMap<>();
    
    private static Map<String, java.util.List<Reading>> readings = new HashMap<>();
    
    public static Map<String, Room> getRooms() {
        return rooms;
    }
    
    
    public static Map<String, Sensor> getSensors() {
        return sensors;
    }
    
    public static java.util.List<Reading> getReadingsForSensor(String sensorId) {
    return readings.getOrDefault(sensorId, new java.util.ArrayList<>());
}

    public static void addReading(String sensorId, Reading reading) {
        readings.putIfAbsent(sensorId, new java.util.ArrayList<>());
        readings.get(sensorId).add(reading);
    }

    public static void updateSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }
    
}