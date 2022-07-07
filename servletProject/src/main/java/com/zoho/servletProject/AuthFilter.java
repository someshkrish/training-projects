package com.zoho.servletProject;

import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class AuthFilter extends HttpFilter {

    public AuthFilter() {
        super();
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
	
		PrintWriter out = res.getWriter();
		
		if(req.getRequestURI().equals("/servletProject/home.jsp")) {
	    	res.sendRedirect("http://localhost/servletProject/error.jsp");
	    	return;
	    }
		
		else if(req.getRequestURI().startsWith("/servletProject/api/v1/getDetails/")) {
			HttpSession session = req.getSession(false);
			String[] pathInfo = req.getPathInfo().split("/");
			
			String name = pathInfo[1];
			String id = pathInfo[2];
		
			if(session == null || session.getAttribute("uname") == null || !session.getAttribute("uname").equals(name)) {
				res.sendRedirect("http://localhost/servletProject/login.jsp");
				return;
		    } else if(session.getAttribute("uname").equals(name)) {
		    	chain.doFilter(request, response);
		    }
	    } else {
	    	out.println("Inside filter");
	    }
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
