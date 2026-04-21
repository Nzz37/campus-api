/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campusapi;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private static Map<String, Room> rooms = new HashMap<>();
    // Add the sensors map:
    private static Map<String, Sensor> sensors = new HashMap<>();
    
    public static Map<String, Room> getRooms() {
        return rooms;
    }
    
    // Add the getter for sensors:
    public static Map<String, Sensor> getSensors() {
        return sensors;
    }
}