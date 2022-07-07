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
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class TicketCancellation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name="jdbc/postgresql")
	private DataSource datasource;
	private Connection conn;
	
    public TicketCancellation() {
        super();
    }

    public class ReleasedTicket {
    	int cabin_no;
    	String berth_type;
    }
    
    public void init() throws ServletException  {
		try {	
			conn = datasource.getConnection();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    
    public ReleasedTicket doUpgrade(int released_cb_no, String released_berth_type, String current_berth) throws SQLException{
    	Statement stmt = null;
    	stmt = conn.createStatement();
    	ResultSet rs = null;
    	
    	ReleasedTicket rtkt = new ReleasedTicket();
    	
    	rtkt.berth_type = released_berth_type;
    	rtkt.cabin_no = released_cb_no;
    	
    	String status = null;
    	String current_berth_pnr = null;
    	String current_berth_pid = null;
    	String current_berth_pname = null;
    	String seat_status = null;
    	int current_berth_page;
    	int current_berth_cabin; 
    	
    	if(current_berth.equals("SL")) {    		
    		status = "RAC"; //current Status
    	} else {
    		status = "WL";
    	}
    	
    	// Queries
    	String retrieveQuery = "select cabin_no, pnr, passenger_id, name, age from booking_table where passenger_id = ("
    			+ "select passenger_id from pnr_status where current_status = '"+ status +"' order by booked_at asc limit 1 )";
    	System.out.println(retrieveQuery);
    	rs = stmt.executeQuery(retrieveQuery);
    	
    	if(rs.next()) {    		
    		current_berth_pnr = rs.getString("pnr");
    		current_berth_pid = rs.getString("passenger_id");
    		current_berth_cabin = rs.getInt("cabin_no");
    		current_berth_pname = rs.getString("name");
    		current_berth_page = rs.getInt("age");
    		
    		System.out.println("Upgrading: " + current_berth_pnr+ " " +current_berth_pid+ " " +current_berth_cabin+" "+current_berth_pname+" "+current_berth_page);
    		
    		String current_berth_upgraded_status = null;
    		
    		if(released_berth_type.equals("SL")) {
    			seat_status = "RAC";
    			current_berth_upgraded_status = "RAC";
    		} else if(!released_berth_type.equals("WL")) {
    			seat_status = "AVL";
    			current_berth_upgraded_status = "CNF";
    		}
    		
    		// Constructing Queries
    		String updatePnrStatus = "update pnr_status set current_status = '"+current_berth_upgraded_status+"' where pnr = '"+current_berth_pnr+"' and passenger_id = '"+current_berth_pid+"'";
    		String updateBerthIncrement = "update berth set available_seats = available_seats+1 where berth_type='"+current_berth+"' and cabin_no = '"+current_berth_cabin+"'";
    		String updateBerthDecrement = "update berth set available_seats = available_seats-1 where berth_type='"+released_berth_type+"' and cabin_no = '"+ released_cb_no +"'";
    		String updateBookingTableRemoving = "update booking_table set pnr=null, current_status='"+status+"', name='', age=null, passenger_id='' where passenger_id='"+current_berth_pid+"'"; 
    		String updateBookingTableAdding = 
    		"update booking_table set pnr = "+ current_berth_pnr +", name = '"+current_berth_pname + "', age ="+ current_berth_page +", current_status = '"+ current_berth_upgraded_status +"', passenger_id = '"+ current_berth_pid +"' FROM (SELECT * FROM booking_table WHERE berth_type = '" + 
    				released_berth_type + "' and cabin_no = "+ released_cb_no +" and current_status = '"+ seat_status +"'FETCH FIRST 1 ROW ONLY) AS subquery WHERE booking_table.seat_no = subquery.seat_no";
    	
    		conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			stmt.executeUpdate(updatePnrStatus);
			stmt.executeUpdate(updateBerthIncrement);
			stmt.executeUpdate(updateBerthDecrement);
			stmt.executeUpdate(updateBookingTableRemoving);
			stmt.executeUpdate(updateBookingTableAdding);
			
			conn.commit();
			conn.setAutoCommit(true);
			
			rtkt.berth_type = current_berth;
			rtkt.cabin_no = current_berth_cabin;
			
    	}
    	
    	return rtkt;
    }
    
    public boolean doRacOrWlUpgrade(String released_cabin_no, String released_berth_type) throws SQLException{
    	
    	ReleasedTicket rtkt = new ReleasedTicket();
    	
    	int cb_no = Integer.parseInt(released_cabin_no); 
    	
    	if(released_berth_type.equals("SL")) {
    		System.out.println("upgrading wl");
    		doUpgrade(cb_no, released_berth_type, "WL");
    	} else if(!released_berth_type.equals("WL")) {
    		System.out.println("Upgrading both sl and wl");
    		rtkt = doUpgrade(cb_no, released_berth_type, "SL");
    		doUpgrade(rtkt.cabin_no,rtkt.berth_type, "WL");
    	}
    	
    	return true;
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Statement stmt = null;
		
		String pid = request.getParameter("pid");
		String pnr = request.getParameter("pnr");
		String cabin_no = request.getParameter("cabin_no");
		String berth_type = request.getParameter("berth_type");
		
		// logic to set the current_status of the seat in the booking table
		String status = "AVL";
		
		if(berth_type.equals("SL")) {
			status = "RAC";
		}
		
		if(berth_type.equals("WL")) {
			status = "WL";
		}
    	
		// writing queries
		String erasePnrRecord = "delete from pnr_status where passenger_id='" + pid + "'";
		String bookingQuery = "update booking_table set pnr=null, current_status='"+status+"', name='', age=null, passenger_id='' where passenger_id='"+pid+"'";
		String countUpdateQuery = "update berth set available_seats = available_seats+1 where berth_type = '" + berth_type + "' and cabin_no =" + cabin_no + "";
		
		try {			
			// creating statements and executing the queries as transaction
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			stmt.execute(erasePnrRecord);
			stmt.executeUpdate(bookingQuery);
			stmt.executeUpdate(countUpdateQuery);
			
			conn.commit();
			conn.setAutoCommit(true);
			
			// Ticket Upgrade Steps
			if(!berth_type.equals("WL")) {
				boolean upgraded = doRacOrWlUpgrade(cabin_no, berth_type);
				
				if(upgraded) {
					System.out.println("Upgraded Successfully.");
				}
			}
			
			
			request.setAttribute("cancellation", "true");
			request.setAttribute("msg", "Ticket Cancelled Successfully.");
			request.setAttribute("pid", pid);
			request.setAttribute("pnr", pnr);
			request.setAttribute("cabin_no", cabin_no);
			request.setAttribute("berth_type", berth_type);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("cancellation", "false");
			request.setAttribute("msg", "Ticket Cancellation Unsuccessful.");
		}
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/CancelledTicketDetails.jsp");
		requestDispatcher.forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
