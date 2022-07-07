<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="ISO-8859-1">
<title>Cancel Ticket</title>
<style>
table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

td, th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}
</style>
</head>

<body>
<label for="pnr">Enter PNR:</label>
<form class="pnrform" name="pnrForm">
<input type="text" id="pnr" name="pnr" minlength="1" maxlength="6" required><br><br>
<input type="submit" value="Get Status">
</form>
<br> <br>
<div class="PnrStatus">

<table class="pnrTable">
  
</table>
</div>

</body>
<script>

  var table = document.querySelector(".pnrTable");
  document.querySelector(".pnrform").addEventListener("submit", (e) => {
	  e.preventDefault();
	  table.innerHTML="<tr>"+
		    "<th>S No.</th>"+
		    "<th>Passenger Id</th>"+
		    "<th>Name</th>"+
		    "<th>Age</th>"+
		    "<th>Booking Status</th>"+
		    "<th>Current Status</th>"+
		    "<th>Seat No</th>"+
		    "<th>Berth Type</th>"+
		    "<th>Cabin No</th>"+
		    "<th>Cancellation</th>"+
		  "</tr>";
	  const pnr = document.forms["pnrForm"].elements['pnr'].value;
      
	  const params = {
    		  pnr
      }
	  
	  let responseData;
      
	  fetch("http://localhost/RailwayReservation/api/v1/pnrstatus", {
		    method: "POST",
		    body: JSON.stringify(params),
		    headers: {
		      "Content-Type": "application/json"
		    }
	  })
	  .then(function(response) {
	    return response.json();
	  }).then(function(data) {
		 responseData = data;
		 
		 responseData.forEach((el, index) => {
			 
			 
			 let name = el.name;
			 let age = el.age;
			 let booking_status = el.booking_status;
			 let current_status = el.current_status;
			 let seat_no = el.berth;
			 let slno = index+1;
			 let pid = el.pid;
			 let pnr = el.pnr;
			 let berth_type = el.berth_type;
			 let cabin_no = el.cabin_no;
			 
			 // ---------------------------------
			 
			 let format_seat_no = seat_no;
			 let format_berth_type = berth_type;
			 let format_cabin_no = cabin_no;
			 
			 if(seat_no.startsWith("NA")){
				 format_seat_no = "-";
			 }
			 
			 if(berth_type.startsWith("WL")){
				 format_berth_type = "-";
			 }
			 
			 if(cabin_no === "0"){
				 format_cabin_no = "NA";
			 }
			 
			 // -----------------------------------
             let form = '<form class="cancel-btn-form" name="cancelForm" action="http://localhost/RailwayReservation/api/v1/cancelReservation"><input type="hidden" name="pid" value="'+pid+'" /><input type="hidden" name="pnr" value="'+pnr+'" /><input type="hidden" name="cabin_no" value="'+cabin_no+'" /><input type="hidden" name="berth_type" value="'+berth_type+'" /><input type="Submit" value="Cancel"></form>';

             let record = "<tr><td>" + slno + "</td><td>" + pid +"</td><td>"+ name +"</td><td>"+ age +"</td><td>"+ booking_status +"</td><td>"+ current_status +"</td><td>"+ format_seat_no +"</td><td>"+format_berth_type+"</td><td>"+format_cabin_no+
			 "</td><td>"+form+"</td></tr>";
             table.insertAdjacentHTML("beforeend", record);
             
		 })
		 
	  });
  })
</script>

</html>