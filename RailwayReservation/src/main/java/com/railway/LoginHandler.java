package com.railway;

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

public class LoginHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Resource(name="jdbc/postgresql")
	private DataSource datasource;
	private Connection conn;
	
    public LoginHandler() {
        super();
    }

    public void init() throws ServletException  {
		try {	
			conn = datasource.getConnection();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		HttpSession session = request.getSession();
		
		Statement stmt = null;
		ResultSet rs = null;
		
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		
		try {
			String sql = "SELECT * FROM user_record WHERE name = '" + name + "'"; 
		    stmt = conn.createStatement();
		    
		    rs = stmt.executeQuery(sql);
		    boolean userexist = false;
		    
		    while(rs.next()) {
		    	userexist = true;
		    	String obtainedPassword = rs.getString("password");
		    	if(obtainedPassword.equals(password)) {
//		    		out.println("Authentication successful.");
		    		session.setAttribute("LoggedIn", "true");
		    		session.setAttribute("uname", name);
		    		session.setMaxInactiveInterval(60);
		    		response.sendRedirect("http://localhost/RailwayReservation/Welcome.jsp");
		    		
		    	} else {
//		    		out.println("Ïnvalid password.");
		    		request.setAttribute("msg", "Invalid Password.");
		    		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/Login.jsp");
		    		requestDispatcher.forward(request, response);
		    	}
		    }
		    
		    if(!userexist) {
//		    	out.println("Invalid user name.");
		    	request.setAttribute("msg", "Invalid User name.");
	    		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/Login.jsp");
	    		requestDispatcher.forward(request, response);
		    }
		    
		} catch(Exception e) {
			
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
