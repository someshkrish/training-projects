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
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import java.util.*;

public class Bookticket extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name="jdbc/postgresql")
	private DataSource datasource;
	private Connection conn;
	private int cabin_size = 2;
	
    public Bookticket() {
        super();
    }
    
    private class Person {
    	String name;
    	int age;
    	String preferred_berth;
    	
    	Person(String name, int age, String berth){
    		this.name = name;
    		this.age = age;
    		this.preferred_berth = berth;
    	}
    }
    
    private class CabinSeats {
    	String berth_type;
    	int available_seats;
    	String next_berth;
    }

    public void init() throws ServletException  {
		try {	
			conn = datasource.getConnection();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    
    private String pnrGenerator() {
    	Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
    
    private String passengerIdGenerator() {
    	Random rnd = new Random();
        int number = rnd.nextInt(9999);

        // this will convert any number sequence into 6 character.
        return "p"+String.format("%04d", number);
    }
    
    private ResultSet queryExecutor (String sql) throws SQLException {
    	Statement stmt = null;
		ResultSet rs = null;
		
		System.out.println(sql);
		
        stmt = conn.createStatement();
	    rs = stmt.executeQuery(sql);
	    
	    return rs;
    }
    
    private HashMap<String, CabinSeats> cabinSeatsCollector (int cabin_no) throws SQLException{
		
		HashMap<String, CabinSeats> seats = new HashMap<>();
		
		String sql = "SELECT * FROM berth where cabin_no = " + cabin_no; 
	    
		ResultSet rs = queryExecutor(sql);
		
	    // Preparing the available seats container
	    while(rs.next()) {
	    	CabinSeats seat = new CabinSeats();
	    	seat.berth_type = rs.getString("berth_type");
	    	seat.available_seats = rs.getInt("available_seats");
	    	seat.next_berth = rs.getString("next_berth");	
	    	seats.put(seat.berth_type , seat);
	    }
	    
	    return seats;
    }
    
    private boolean checkAvailability(int cabin_no, HashMap<String, Integer> berth) {
    	try {
    		
    		HashMap<String, CabinSeats> seats = new HashMap<>();
            seats = cabinSeatsCollector(cabin_no);
            
		    // Login for checking availability
		    for(Map.Entry<String, Integer> entry : berth.entrySet()) {
		    	String chosenBerth = entry.getKey();
		    	Integer seatsRequired = entry.getValue();
		    	
		    	if(seats.get(chosenBerth).available_seats < seatsRequired) {
		    		return false;
		    	}
		    }
		    
		    return true;
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    private int allotInACabin( HashMap<String, Integer> berth) {
    	for(int i=1; i<=cabin_size; i++) {
    		if(checkAvailability(i, berth)) {
    			return i;
    		}
    	}
    	return 0;
    }
    
    private List<Person> bookSeatsInACabin(String pnr, int allotedCabin, List<Person> passengers) throws SQLException{
    	List<Person> bookedPersons = new LinkedList<>();
    	for(Person p: passengers) {
    		
    		boolean booked = bookForThisPerson(pnr, p.preferred_berth, "AVL", "CNF", p, allotedCabin);
    		
    		if(booked) {
    			bookedPersons.add(p);
    		}
    		if(!booked) {
    			System.out.println("Error In Booking. Line no. 141");
    			break;
    		}
    	}
    	
    	if(!bookedPersons.isEmpty()) {    		
    		passengers.removeAll(bookedPersons);
    	}
    	
    	return passengers;
    }
    
    private boolean bookForThisPerson(String pnr, String allotedBerth,String seatStatus, String bookingStatus, Person p, int cabin_no) throws SQLException {
    	Statement stmt = null;
    	
    	String name = p.name;
		int age = p.age;
		String preferredBirth = allotedBerth;
		String status = bookingStatus;
		String pid = passengerIdGenerator();
		
		// writing queries
		String recordBooking = "insert into pnr_status (pnr, name, current_status, booking_status, passenger_id) values ("+ pnr + ",'" + name + "', '"+ status +"', '"+ status +"', '"+ pid +"')";
		String bookingQuery = "update booking_table set pnr = "+ pnr +", name = '"+ name + "', age ="+ age +", current_status = '"+ status +"', passenger_id = '"+ pid +"' FROM (SELECT * FROM booking_table WHERE berth_type = '" + preferredBirth + "' and cabin_no = "+ cabin_no +" and current_status = '"+ seatStatus +"'FETCH FIRST 1 ROW ONLY) AS subquery WHERE booking_table.seat_no = subquery.seat_no";
		String countUpdateQuery = "update berth set available_seats = available_seats-1 where berth_type = '" + preferredBirth + "' and cabin_no =" + cabin_no + "";
		
		// creating statements and executing the queries as transaction
		conn.setAutoCommit(false);
	    stmt = conn.createStatement();
	    
	    stmt.execute(recordBooking);
	    stmt.executeUpdate(bookingQuery);
	    stmt.executeUpdate(countUpdateQuery);
	    
	    conn.commit();
	    conn.setAutoCommit(true);
    	
    	return true;
    }
    
    private List<Person> optimalSeatAllocationRACorWL(String allotedPnr, int cabin_no, List<Person> passengers, String preferredBerth, String bookingStatus){
    	try {
    		HashMap<String, CabinSeats> seatsInACabin = cabinSeatsCollector(cabin_no);
    		List<Person> bookedPersons = new LinkedList<>();
    		
    		for(Person p: passengers) {
    		    if(seatsInACabin.get(preferredBerth).available_seats >= 1) {
    				boolean booked = bookForThisPerson(allotedPnr, preferredBerth, bookingStatus, bookingStatus, p, cabin_no);
    				if(booked) {
    					seatsInACabin.get(preferredBerth).available_seats -= 1;
    					bookedPersons.add(p);
    				} else {
    					System.out.println("Error booking RAC. Line 188");
    				    break;
    				}
    			} else {
    				break;
    			}
    			
    		}
    		
    		if(!bookedPersons.isEmpty()) {    			
    			passengers.removeAll(bookedPersons);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return passengers;
    }
    
    private List<Person> optimalSeatAllocation(String allotedPnr, int cabin_no, List<Person> passengers) {
    	try {
    		HashMap<String, CabinSeats> seatsInACabin = cabinSeatsCollector(cabin_no);
    		List<Person> bookedPersons = new LinkedList<>();
    		
    		for(Person p : passengers) {
    			
    			String preferredBerth = p.preferred_berth;
    			boolean found = false;
    			
    			// iterating and trying to allocate the group of passengers in near berths available
    			while(!found) {    				
    				if(seatsInACabin.get(preferredBerth).available_seats >= 1) {
    					boolean booked = bookForThisPerson(allotedPnr, preferredBerth, "AVL", "CNF", p, cabin_no);
    					
    					if(booked) {
    						seatsInACabin.get(preferredBerth).available_seats -= 1;
    						found = true;
    					} else {
    						System.out.println("Error in booking the optimal berth.");
    						break;
    					}
    					
    				} else {
    					// looking next preferred berth
    					preferredBerth = seatsInACabin.get(preferredBerth).next_berth;
    					
    					// Condition to check whether all the berths are looked or not
    					if(preferredBerth.equals("NA")) {
    						break;
    					}
    				}
    			}
    			if(!found) {
    				break;
    			} else {
    				bookedPersons.add(p);
    			}
    		}
    		
    		if(!bookedPersons.isEmpty()) {    			
    			passengers.removeAll(bookedPersons);
    		}
   
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return passengers;
    }
    
    private List<Person> bookSeatsOptimally(String allotedPnr, int totalTickets, List<Person> passengers) throws SQLException {
    	
    	HashMap<Integer, Integer> isSeatsAvailable = new HashMap<>();
    	
    	String sql = "select cabin_no, sum(available_seats) from berth where berth_type != 'SL' group by cabin_no;";
    	ResultSet rs = queryExecutor(sql);
    	
    	while(rs.next()) {
    		Integer cabin_no = rs.getInt("cabin_no");
    		Integer available_seats = rs.getInt("sum");
    		
    		isSeatsAvailable.put(cabin_no, available_seats);
    	}
    	
    	//PASSENGER COLLECTION PIPELINE STARTS HERE
    	
    	// Try allocating normal berths
    	for(int i=1; i<=cabin_size; i++) {
    		if(isSeatsAvailable.get(i) > 0) {
    			passengers = optimalSeatAllocation(allotedPnr, i, passengers);
    		}
    	}
    	
    	if(passengers.isEmpty()) {
    		System.out.println("All passengers are successfully alloted a seat.");
    		return passengers;
    	}
    	
    	// Try allocating RAC
    	for(int i=1; i<=cabin_size; i++) {
    		passengers = optimalSeatAllocationRACorWL(allotedPnr, i, passengers, "SL", "RAC");
    	}
    	
    	if(passengers.isEmpty()) {
    		System.out.println("All passengers are successfully alloted a seat in normal berth or RAC.");
    		return passengers;
    	}
    	
    	// Try allocating WL
    	passengers = optimalSeatAllocationRACorWL(allotedPnr, 0, passengers, "WL", "WL");
    	
    	if(passengers.isEmpty()) {
    		System.out.println("Some passengers are in waiting list...");
    		return passengers;
    	}
    	
    	// Cancel ticket booking
    	System.out.println("Some tickets are cancelled");
    	return passengers;
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		int totalTickets = Integer.parseInt(request.getParameter("count"));
		
		List<Person> passengers = new LinkedList<>();
		List<Person> unbookedPassengers = new LinkedList<>();
		
		HashMap<String, Integer> berth = new HashMap<>();
		
		for(int i=1; i<=totalTickets; i++) {
			String name = request.getParameter("name"+i);
			int age = Integer.parseInt(request.getParameter("age" + i));
			String preferred_berth = request.getParameter("berth" + i);
			
			if(age <= 5) {
				continue;
			}
			
			if(berth.containsKey(preferred_berth)) {
				int current_value = berth.get(preferred_berth) + 1;
				berth.put(preferred_berth, current_value);
			} else {
				berth.put(preferred_berth, 1);
			}
			
			Person passenger = new Person(name, age, preferred_berth);
			passengers.add(passenger);
		}
		
		String allotedPnr = pnrGenerator();
		
		// Checking the availability of berth in single cabin
		int allotedCabin = allotInACabin(berth);
		
		// Allocate all seats in one cabin
		// if allotedCabin = 0 no cabin is alloted
		if(allotedCabin > 0) {
			try {				
				unbookedPassengers = bookSeatsInACabin(allotedPnr, allotedCabin, passengers);
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		// Use optimal Allocation
		else {
			try {
				unbookedPassengers = bookSeatsOptimally(allotedPnr, totalTickets, passengers);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		String msg = "";
		int success = totalTickets - unbookedPassengers.size();
		int failure = unbookedPassengers.size();
		
		if(unbookedPassengers.isEmpty()) {
			msg = "All passengers are allocated a seat.";
			out.println(msg);
		} else {
			msg = "Some passengers are not allocated a seat.";
			out.println(msg);
			System.out.println(unbookedPassengers.toString());
		}
		
		request.setAttribute("ticketBookingStatus", "true");
		request.setAttribute("msg", msg);
		request.setAttribute("success",success);
		request.setAttribute("failure", failure);
		request.setAttribute("pnr", allotedPnr);
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/BookingStatus.jsp");
		requestDispatcher.forward(request, response);
	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
