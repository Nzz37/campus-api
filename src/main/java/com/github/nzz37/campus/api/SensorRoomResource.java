/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campusapi;

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
        // Basic safety check
        if (newRoom.getId() == null || newRoom.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Room ID is required\"}")
                           .build();
        }
        
        // Save it to our HashMap (since databases are forbidden)
        DataStore.getRooms().put(newRoom.getId(), newRoom);
        
        // Return appropriate feedback upon success (HTTP 201 Created)
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
}
