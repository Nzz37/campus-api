/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campus.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        String json = "{"
                + "\"version\": \"1.0\","
                + "\"adminContact\": \"Nzz37@my.westminster.ac.uk\","
                + "\"resources\": {"
                + "  \"rooms\": \"/api/v1/rooms\","
                + "  \"sensors\": \"/api/v1/sensors\""
                + "}"
                + "}";
        return Response.ok(json).build();
    }
}
