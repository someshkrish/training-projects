<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Booking Page</title>
</head>
<body>

<%
  if(session.getAttribute("LoggedIn") != null && session.getAttribute("LoggedIn").equals("true")) {
	  String uname = (String)session.getAttribute("uname");
	  out.println("<h3>Welcome " + uname + "!</h3>");
  }
%>

<hr>

<form name="count-form" class="countForm">
  <label for="count">Enter the number of persons:</label>
  <input type="number" id="count" name="count" min=1><br><br>
  <input type="submit" value="Confirm">
</form>

<hr>

<form action="http://localhost/RailwayReservation/api/v1/bookticket" method="POST" name="personRecordForm" class="personDetails">
 
</form>
<br>
${msg}
<%
  if(request.getAttribute("pnr") != null){
	  out.println("Your PNR is: " + request.getAttribute("pnr"));
  }
%>
</body>
<script>
    const fieldGenerator = (count) => {
    	let name = "name" + count;
    	let age = "age" + count;
    	let berth = "berth" + count;
    	let LB = "LB" + count;
    	let MB = "MB" + count;
    	let UB = "UB" + count;
    	// let SL = "SL" + count;
    	let SU = "SU" + count;
    	
    	let field = '<p>Enter the details of person '+ count + "</p>"+ 
    		  '<label for="'+name+'">Name:</label>'+
    		  '<input type="text" id="'+name+'" name="'+name+'" minlength="3" required><br><br>'+
    		  '<label for="'+age+'">Age:</label>'+
    		  '<input type="number" id="'+age+'" name="'+age+'" min="1" max="120" required><br><br>'+
    		  '<p>Please select your preferred berth:</p>'+
    		  '<input type="radio" id="'+LB+'" name="'+berth+'" value="LB" required>'+
    		  '<label for="'+LB+'">Lower Beth (LB)</label>'+
    		  '<input type="radio" id="'+MB+'" name="'+berth+'" value="MB">'+
    		  '<label for="'+MB+'">Middle Berth (MB)</label>'+
    		  '<input type="radio" id="'+UB+'" name="'+berth+'" value="UB">'+
    		  '<label for="'+UB+'">Upper Beth (UB)</label>'+
    		  //'<input type="radio" id="'+SL+'" name="'+berth+'" value="SL">'+
    		  //'<label for="'+SL+'">Side Lower (SL)</label>'+
    		  '<input type="radio" id="'+SU+'" name="'+berth+'" value="SU">'+
    		  '<label for="'+SU+'">Side Upper (SU)</label><br><br>'+
    		  '<hr>';
        return field;
    }
    
    document.querySelector(".countForm").addEventListener('submit', (e) => {
    	e.preventDefault();
    	let count = document.forms["count-form"].elements['count'].value;
    	const parentForm = document.querySelector(".personDetails");
    	parentForm.textContent = "";
    	parentForm.insertAdjacentHTML("afterbegin", '<input type="hidden" id="count" name="count" value="'+ count +'">')
    	if(count>0){
    		parentForm.insertAdjacentHTML("beforeend", '<input type="submit" value="Book Ticket">');
    	}
    	
    	while(count){
    		let field = fieldGenerator(count);
    		parentForm.insertAdjacentHTML("afterbegin", field);
    		count = count-1;		
    	}
    	
    	
    })
</script>
</html>