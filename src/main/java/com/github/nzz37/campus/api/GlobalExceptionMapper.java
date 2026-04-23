/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.nzz37.campus.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider 
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        exception.printStackTrace(); 
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR) // HTTP 500
                       .entity("{\"error\": \"Internal Server Error\", \"message\": \"An unexpected error occurred.\"}")
                       .type("application/json")
                       .build();
    }
}
