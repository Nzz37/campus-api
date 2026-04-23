/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campus.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SensorReadingResource {
    
    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorReadings() {
        return Response.ok(DataStore.getReadingsForSensor(sensorId)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(Reading reading) {
        Sensor parentSensor = DataStore.getSensors().get(sensorId);
        
        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorUnavailableException("Cannot accept readings: Sensor is currently in MAINTENANCE mode.");
        }

        parentSensor.setCurrentValue(reading.getValue());
        DataStore.updateSensor(parentSensor);
        DataStore.addReading(sensorId, reading);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
