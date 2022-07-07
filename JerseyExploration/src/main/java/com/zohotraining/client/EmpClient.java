package com.zohotraining.client;

import com.zohotraining.model.EmpRequest;
import com.zohotraining.model.EmpResponse;
import com.zohotraining.model.ErrorResponse;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class EmpClient {
	public static void main(String[] args) {
		String uri = "http://localhost/JerseyExploration/rest/emp/getEmp";
		EmpRequest request = new EmpRequest();
		// set id as 1 for OK response
		request.setId(1);
		request.setName("PK");
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(uri);			
			
			Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(request, MediaType.APPLICATION_JSON), Response.class);
			
			System.out.println(response.getStatus());
			if (response.getStatus() == 200) {
				EmpResponse empResponse = response.readEntity(EmpResponse.class);
				System.out.println(empResponse.getId() + "::" + empResponse.getName());
			} else {
				ErrorResponse exc = response.readEntity(ErrorResponse.class);
				System.out.println(exc.getErrorCode());
				System.out.println(exc.getErrorId());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
