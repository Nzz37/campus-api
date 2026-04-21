/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campusapi;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
    // These static maps act as our database tables
    private static Map<String, Room> rooms = new HashMap<>();
    
    public static Map<String, Room> getRooms() {
        return rooms;
    }
}