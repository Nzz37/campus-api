/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campus.api;

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
    
    // 2. GET /sensors: List all sensors with Advanced Search (Part 6)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchSensors(@QueryParam("type") String type, @QueryParam("status") String status) {
        java.util.List<Sensor> results = new java.util.ArrayList<>();
        
        for (Sensor s : DataStore.getSensors().values()) {
            boolean matches = true; // Assume it matches until proven otherwise
            
            // If the user asked for a specific type, check it
            if (type != null && !type.isEmpty() && !s.getType().equalsIgnoreCase(type)) {
                matches = false;
            }
            
            // If the user asked for a specific status, check it
            if (status != null && !status.isEmpty() && !s.getStatus().equalsIgnoreCase(status)) {
                matches = false;
            }
            
            // If it survived the filters, add it to the final list
            if (matches) {
                results.add(s);
            }
        }
        
        return Response.ok(results).build();
    }

    // 3. GET /sensors/{sensorId}: Get specific sensor details
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Sensor not found\"}")
                           .build();
        }
        return Response.ok(sensor).build();
    }
    
    // 4. PUT /sensors/{sensorId}: Update an existing sensor
    @PUT
    @Path("/{sensorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSensor(@PathParam("sensorId") String sensorId, Sensor updatedSensor) {
        if (!DataStore.getSensors().containsKey(sensorId)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Sensor not found\"}")
                           .build();
        }
        
        // Keep the ID consistent
        updatedSensor.setId(sensorId);
        DataStore.getSensors().put(sensorId, updatedSensor);
        
        return Response.ok(updatedSensor).build();
    }

    // 5. DELETE /sensors/{sensorId}: Remove a sensor
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensors().remove(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        // Clean up: Remove the sensor ID from the Room's list too
        Room room = DataStore.getRooms().get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().remove(sensorId);
        }
        
        return Response.noContent().build(); // Success 204
    }
    
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
}
}