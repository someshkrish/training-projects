package com.zoho.servletProject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Logger logger = null;
	
	public void init() throws ServletException  
	{
		try {
            logger = Logger.getLogger(this.getClass().getName());		
    
            FileHandler fileHandler = new FileHandler("C:\\Users\\somesh-pt5225\\java_logout.log");
            logger.addHandler(fileHandler);
		} catch(Exception e) {
			logger.warning("Error in logger instantiation in logout servlet." + e.getStackTrace());
			e.printStackTrace();
		}
		
	}
    public logout() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			request.getSession(false).invalidate();
			logger.info("Session Invalidated...");
		} catch (Exception e) {
			logger.info("Session Already expired...");
		} finally {
			logger.info("Successful LOGOUT. Redirecting to login.jsp");
			response.sendRedirect("http://localhost/servletProject/login.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
