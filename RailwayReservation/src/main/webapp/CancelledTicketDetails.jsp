<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Details</title>
</head>
<body>
<h2>Cancellation Details</h2>
<hr>
<br>
<%
  if(request.getAttribute("cancellation").equals("true")){
	  out.println("<h3>Message: </h3>");
	  out.println("<p>"+request.getAttribute("msg")+"</p><br>");
	  
	  out.println("<h3>Passenger Id: </h3>");
	  out.println("<p>"+request.getAttribute("pid")+"</p><br>");
	  
	  out.println("<h3>PNR: </h3>");
	  out.println("<p>"+request.getAttribute("pnr")+"</p><br>");
	  
	  out.println("<h3>Cabin No: </h3>");
	  out.println("<p>"+request.getAttribute("cabin_no")+"</p><br>");
	  
	  out.println("<h3>Berth Type: </h3>");
	  out.println("<p>"+request.getAttribute("berth_type")+"</p><br>");
  } else {
	  out.println("<h3>Message: </h3>");
	  out.println("<p>"+request.getAttribute("msg")+"</p><br>");
  }
%>

<hr>

<p>Go to <a href="http://localhost/RailwayReservation/Welcome.jsp">Home Page</a></p>
</body>
</html>