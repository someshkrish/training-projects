package com.zoho.servletProject;

import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
//import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.sql.DataSource;

//@WebServlet("/api/v1/login")
//http://localhost/servletProject/login.jsp
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Resource(name="jdbc/postgresql")
	private DataSource datasource;
	private Connection conn = null;
	private Logger logger = null;
	
	public void init() throws ServletException  
	{
		try {
			conn = datasource.getConnection();
            logger = Logger.getLogger(this.getClass().getName());		
    
            FileHandler fileHandler = new FileHandler("C:\\Users\\somesh-pt5225\\java_login.log");
            logger.addHandler(fileHandler);
		} catch(Exception e) {
			logger.warning("Error in db connection or logger instantiation in login servlet." + e.getStackTrace());
			e.printStackTrace();
		}
		
	}
    public login() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// step1: setup the PrintWriter
		HttpSession session = request.getSession();
				
//		step2: get a connection to a database
		Statement stmt = null;
		ResultSet rs = null;
		
		
		String parameter = request.getParameter("userName");
		try {
			//step3: create a SQL statement
//			String query = "select exists (select true from person where name= '" + parameter + "');";
			String sql = "SELECT * FROM person WHERE name = '" + parameter + "';";
			stmt = conn.createStatement();
					
			//step4: execute a query
			rs = stmt.executeQuery(sql);
            
			boolean recordFound = rs.next();
			
			//step5: process the result set
			if(recordFound) {
				logger.info("Record Found...");
				String name = rs.getString("name");
				String id = rs.getString("id");
				 
		        session.setAttribute("uname",name);
		        session.setAttribute("id", id);
		        session.setMaxInactiveInterval(10);
		        
		        logger.info("Redirecting to the get details page... AUTHENTICATED!!!");
				response.sendRedirect("http://localhost/servletProject/api/v1/getDetails/" + name + "/" + id);
				logger.info("Successfully Redirected");
			} else {
				request.setAttribute("msg", "Invalid Credentials!!!");
				RequestDispatcher requestDispatcher = request.getRequestDispatcher("/login.jsp");
				
				logger.info("NOT AUTHENTICATED!!! Forwarding the control to login.jsp.");
				requestDispatcher.forward(request, response);
			}
		} catch (Exception e) {
			logger.warning("Error in getting validating user data." + e.getStackTrace());
			e.printStackTrace();
		}
	}
}
