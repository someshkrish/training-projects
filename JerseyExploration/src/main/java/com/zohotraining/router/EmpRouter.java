package com.zohotraining.router;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.zohotraining.exception.EmpNotFoundException;
import com.zohotraining.model.EmpRequest;
import com.zohotraining.model.EmpResponse;
import com.zohotraining.model.ErrorResponse;

import jakarta.xml.bind.JAXBElement;

@Path("/emp")
public class EmpRouter {
	@POST
	@Path("/getEmp")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmp(EmpRequest empRequest) throws EmpNotFoundException {
		System.out.println("Inside POST Handler");
		
		EmpResponse empResponse = new EmpResponse();
	
		if (empRequest.getId() == 1) {
			empResponse.setId(empRequest.getId());
			empResponse.setName(empRequest.getName()); 
		} else {
			ErrorResponse errResponse = new ErrorResponse();
			errResponse.setErrorCode("Wrong ID");
			errResponse.setErrorId(2);
			return Response.status(500).entity(errResponse).build();
		}
		return Response.ok(empResponse).build(); 
	}
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEmpReq(@Context HttpServletRequest request) throws EmpNotFoundException {
		System.out.println("inside Session testing");
		HttpSession session = request.getSession();
	    String name = (String) session.getAttribute("uname");
		String res = "It's working..." + name;
		return res;
	}
	
	@GET
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public String registerMe(@Context HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("uname", "somesh"); //default lifetime is 30 mins
		
		return "Success";
	}
	
}
