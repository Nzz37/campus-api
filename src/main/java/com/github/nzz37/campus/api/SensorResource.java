/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campusapi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensors")
public class SensorResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSensor(Sensor newSensor) {
        // 1. Integrity Check: Does the room exist?
        if (!DataStore.getRooms().containsKey(newSensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Cannot register sensor. Room ID '" + newSensor.getRoomId() + "' does not exist.");
        }

        // 2. Save the sensor
        DataStore.getSensors().put(newSensor.getId(), newSensor);
        
        // 3. Update the Room to know about this new sensor
        Room room = DataStore.getRooms().get(newSensor.getRoomId());
        room.getSensorIds().add(newSensor.getId());

        // 4. Return success (HTTP 201)
        return Response.status(Response.Status.CREATED).entity(newSensor).build();
    }
}