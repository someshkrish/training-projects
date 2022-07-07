package com.zohotraining.exceptionmapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.zohotraining.exception.EmpNotFoundException;
import com.zohotraining.model.ErrorResponse;

@Provider
public class EmpNotFoundExceptionMapper implements
		ExceptionMapper<EmpNotFoundException> {

	public EmpNotFoundExceptionMapper() {
	}
	
	public Response toResponse(
			EmpNotFoundException empNotFoundException) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorId(empNotFoundException.getErrorId());
		errorResponse.setErrorCode(empNotFoundException.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
				errorResponse).type(
				MediaType.APPLICATION_XML).build();

	}

}
