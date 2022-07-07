package com.railway;

import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
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
		HttpSession session = req.getSession();
		
		System.out.println("inside filter");
		try {			
			if(session == null || session.getAttribute("LoggedIn") == null || session.getAttribute("LoggedIn").equals("false")){
				System.out.println("Redirecting to signup page.");
				res.sendRedirect("http://localhost/RailwayReservation/Signup.jsp");
				return;
			} else {
				
				if(req.getRequestURI().equals("/RailwayReservation/CancelledTicketDetails.jsp")) {
					if(request.getAttribute("cancellation") == null) {
						res.sendRedirect("http://localhost/RailwayReservation/Error.jsp");
					}
				}
				
				else if(req.getRequestURI().equals("/RailwayReservation/BookingStatus.jsp")) {
					if(request.getAttribute("ticketBookingStatus") == null) {
						res.sendRedirect("http://localhost/RailwayReservation/Error.jsp");
					}
				}
				
				System.out.println("Authenticated.");
				chain.doFilter(request, response);
			}
		} catch(Exception e) {
			System.out.println("Error in filtering.");
			e.printStackTrace();
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
