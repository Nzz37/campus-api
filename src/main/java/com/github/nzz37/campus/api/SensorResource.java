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
        
        if (!DataStore.getRooms().containsKey(newSensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Cannot register sensor. Room ID '" + newSensor.getRoomId() + "' does not exist.");
        }

        DataStore.getSensors().put(newSensor.getId(), newSensor);

        Room room = DataStore.getRooms().get(newSensor.getRoomId());
        room.getSensorIds().add(newSensor.getId());

        return Response.status(Response.Status.CREATED).entity(newSensor).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchSensors(@QueryParam("type") String type, @QueryParam("status") String status) {
        java.util.List<Sensor> results = new java.util.ArrayList<>();
        
        for (Sensor s : DataStore.getSensors().values()) {
            boolean matches = true; 

            if (type != null && !type.isEmpty() && !s.getType().equalsIgnoreCase(type)) {
                matches = false;
            }

            if (status != null && !status.isEmpty() && !s.getStatus().equalsIgnoreCase(status)) {
                matches = false;
            }

            if (matches) {
                results.add(s);
            }
        }
        
        return Response.ok(results).build();
    }

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
        
        updatedSensor.setId(sensorId);
        DataStore.getSensors().put(sensorId, updatedSensor);
        
        return Response.ok(updatedSensor).build();
    }

    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensors().remove(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        Room room = DataStore.getRooms().get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().remove(sensorId);
        }
        
        return Response.noContent().build(); 
    }
    
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
}
}