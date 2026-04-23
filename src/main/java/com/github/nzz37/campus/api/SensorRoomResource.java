/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campus.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/rooms") // This sits under the /api/v1 base path
public class SensorRoomResource {

    // 1. GET /: Provide a comprehensive list of all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        // Fetch all rooms from our static DataStore
        Collection<Room> allRooms = DataStore.getRooms().values();
        return Response.ok(allRooms).build();
    }

    // 2. POST /: Enable the creation of new rooms
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        if (newRoom.getId() == null || newRoom.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Room ID is required\"}").build();
        }

        // --- NEW CONFLICT CHECK ---
        if (DataStore.getRooms().containsKey(newRoom.getId())) {
            return Response.status(Response.Status.CONFLICT) // This is HTTP 409
                           .entity("{\"error\":\"Room with ID " + newRoom.getId() + " already exists\"}")
                           .build();
        }
        // --------------------------

        DataStore.getRooms().put(newRoom.getId(), newRoom);
        return Response.status(Response.Status.CREATED).entity(newRoom).build();
    }

    // 3. GET /{roomId}: Fetch detailed metadata for a specific room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Room not found\"}")
                           .build();
        }
        
        return Response.ok(room).build();
    }
    
    // 4. DELETE /{roomId}: Allow room decommissioning safely
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        
        // Check if the room exists
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Room not found\"}")
                           .build();
        }
        
        // Safety Logic: Prevent deletion if sensors are still active
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room. It is currently occupied by active hardware.");
        }
        
        // If safe, delete the room
        DataStore.getRooms().remove(roomId);
        
        // Return 204 No Content on successful deletion
        return Response.noContent().build();
    }
    
    // 5. GET /{roomId}/sensors: Get list of sensors belonging to this room
    @GET
    @Path("/{roomId}/sensors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorsInRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Room not found\"}")
                           .build();
        }
        
        // Find all sensors that have this roomId
        java.util.List<Sensor> roomSensors = new java.util.ArrayList<>();
        for (Sensor s : DataStore.getSensors().values()) {
            if (s.getRoomId().equals(roomId)) {
                roomSensors.add(s);
            }
        }
        return Response.ok(roomSensors).build();
    }
}
