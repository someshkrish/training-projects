<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Login Page</title>
</head>
<body>
<form action="http://localhost/RailwayReservation/api/v1/login" method="POST">
  <label for="name">Name:</label>
  <input type="text" id="name" name="name"><br><br>
  <label for="password">Password:</label>
  <input type="text" id="password" name="password"><br><br>
  <input type="submit" value="Log in">
</form>
<br/>
${msg}
</body>
</html>