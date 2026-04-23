/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campus.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/rooms") 
public class SensorRoomResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        Collection<Room> allRooms = DataStore.getRooms().values();
        return Response.ok(allRooms).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        if (newRoom.getId() == null || newRoom.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Room ID is required\"}").build();
        }

        if (DataStore.getRooms().containsKey(newRoom.getId())) {
            return Response.status(Response.Status.CONFLICT) 
                           .entity("{\"error\":\"Room with ID " + newRoom.getId() + " already exists\"}")
                           .build();
        }
        DataStore.getRooms().put(newRoom.getId(), newRoom);
        return Response.status(Response.Status.CREATED).entity(newRoom).build();
    }

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
    
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Room not found\"}")
                           .build();
        }
        
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room. It is currently occupied by active hardware.");
        }
        
        DataStore.getRooms().remove(roomId);
        
        return Response.noContent().build();
    }
    
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
        
        java.util.List<Sensor> roomSensors = new java.util.ArrayList<>();
        for (Sensor s : DataStore.getSensors().values()) {
            if (s.getRoomId().equals(roomId)) {
                roomSensors.add(s);
            }
        }
        return Response.ok(roomSensors).build();
    }
    
    @GET
    @Path("/crash")
    public Response testGlobalError() {
        String fakeData = null;
        fakeData.length(); 
    
        return Response.ok().build();
}
}
