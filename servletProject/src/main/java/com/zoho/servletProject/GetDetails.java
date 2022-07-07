package com.zoho.servletProject;

import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.sql.DataSource;

//@WebServlet("/api/v1/getDetails/*")
public class GetDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name="jdbc/postgresql")
	private DataSource datasource;
	private Connection conn;
    private Logger logger = null;
	
    public GetDetails() {
        super();
    }

    public void init() throws ServletException  {
		try {
			logger = Logger.getLogger(this.getClass().getName());	
			conn = datasource.getConnection();
			
			FileHandler fileHandler = new FileHandler("C:\\Users\\somesh-pt5225\\java_getdetails.log");
            logger.addHandler(fileHandler);
		} catch(Exception e) {
			logger.warning("Error in logger instantiation or db connection in getDetails servlet." + e.getStackTrace());
			e.printStackTrace();
		}
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// step1: setup the PrintWriter
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		String name;
		String id;
		String details;

		String[] pathInfo = request.getPathInfo().split("/");
		
		name = pathInfo[1];
		id = pathInfo[2];
		
		out.println(name);
		out.println(id);

		
//		step2: get a connection to a database
		Statement stmt = null;
		ResultSet rs = null;

		try {
			//step3: create a SQL statement
			String sql = "SELECT * FROM person WHERE id = " + id + ";";
			stmt = conn.createStatement();
			
			//step4: execute a query
			rs = stmt.executeQuery(sql);
			
			//step5: process the result set
			while(rs.next()) {
				details = rs.getString("details");
				request.setAttribute("name", name);
				request.setAttribute("id", id);
				request.setAttribute("details", details);
				
				// if route specified startswith "/" then that is relative to the context root path.
				// here it is /localhost/servletProject
				logger.info("Successfully fetched data from db and redirecting to home.jsp.");
				RequestDispatcher requestDispatcher = request.getRequestDispatcher("/home.jsp");
				requestDispatcher.forward(request, response);
			}
		} catch (Exception e) {
			logger.warning("Error in fetching data from db in getdetails servlet." + e.getStackTrace());
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
