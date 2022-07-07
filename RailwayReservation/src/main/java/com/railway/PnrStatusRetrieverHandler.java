package com.railway;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.json.HTTP;
import org.json.JSONObject;

import com.google.gson.Gson;

public class PnrStatusRetrieverHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name="jdbc/postgresql")
	private DataSource datasource;
	private Connection conn;
	
    public PnrStatusRetrieverHandler() {
        super();
    }
    
    private class PNR {
    	private String pnr;
    	
    	@SuppressWarnings("unused")
		PNR(){
    		this.pnr = "";
    	}
    	
    	public String getPnr() {
    		return pnr;
    	}
    }
    
    private class Ticket {
    	public String name;
    	public int age;
    	public String booking_status;
    	public String current_status;
    	public String berth;
    	public int pnr;
    	public String pid;
    	public String berth_type;
    	public String cabin_no;
    }
    
    public void init() throws ServletException  {
		try {	
			conn = datasource.getConnection();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    private ResultSet getPnrDetails(int pnr) {
    	ResultSet rs = null;
    	try {
    		Statement stmt = null;
    		
    		String sql = "select b.pnr, b.name, b.age, b.seat_no, b.cabin_no, b.berth_type, p.current_status, p.booking_status, p.passenger_id from booking_table b inner join pnr_status"+
    		             " p on p.pnr = b.pnr and p.name = b.name where b.pnr = " + pnr;
    		stmt = conn.createStatement();
    		
    		rs = stmt.executeQuery(sql);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return rs;
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json , charset=UTF-8");
		
		// parsing the request and getting the body
		String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		String result = null;
		
		// parsing body string and converting it into ticket object using Gson
		Gson g = new Gson();  
		PNR s = g.fromJson(test, PNR.class);
		int pnrNo = Integer.parseInt(s.getPnr());
        
//		System.out.println(pnrNo);
		
		// getting the pnr details
		ResultSet rs = getPnrDetails(pnrNo);
		List<Ticket> pnrDetails = new LinkedList<>();
		
		try {
			while(rs.next()) {
				Ticket tkt = new Ticket();
				tkt.name = rs.getString("name");
				tkt.age = rs.getInt("age");
				tkt.booking_status = rs.getString("booking_status");
				tkt.current_status = rs.getString("current_status");
				tkt.berth = rs.getString("seat_no");
				tkt.pnr = rs.getInt("pnr");
				tkt.pid = rs.getString("passenger_id");
				tkt.berth_type = rs.getString("berth_type");
				tkt.cabin_no = rs.getString("cabin_no");
				
				pnrDetails.add(tkt);
			}
			
			result = new Gson().toJson(pnrDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.getWriter().write(result);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
