<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Info Page</title>
</head>
<body>

<h3>Booking Update!</h3>

<hr>

<h3>Booking message</h3>
<p> ${msg} </p>

<hr>

<h3>Successfull booking count:</h3>
<p> ${success}</p>

<hr>

<h3>Unsuccessfull booking count:</h3>
<p>
<%
 if(request.getAttribute("failure").equals("0")){
	 out.println(0);
 } else {
	 out.println(request.getAttribute("failure"));
 }
%>
</p>

<hr>

<h3>Your Pnr:</h3>
<p>${pnr}</p>
<hr>

<p>To check the status of your ticket, enter pnr in the satus check page.</p>
<a href="http://localhost/RailwayReservation/PNRstatus.jsp">Click Here</a>

<hr>

<p>Go to <a href="http://localhost/RailwayReservation/Welcome.jsp">Home page</a> </p>

</body>
</html>