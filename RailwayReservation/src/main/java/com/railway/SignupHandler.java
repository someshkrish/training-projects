package com.railway;

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

public class SignupHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name="jdbc/postgresql")
	private DataSource datasource;
	private Connection conn;
	
    public SignupHandler() {
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
		
		Statement stmt = null;
		
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		try {
			String sql = "INSERT INTO user_record( name, password) VALUES( '" + name + "','"+ password +"')";
			stmt = conn.createStatement();
			
			stmt.execute(sql);
			
			request.setAttribute("msg", "Account Created Successfully. Log in to continue.");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/Login.jsp");
    		requestDispatcher.forward(request, response);
			
			out.println("executed successfully.");
			
		} catch (Exception e) {
			e.printStackTrace();
			out.println(e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
