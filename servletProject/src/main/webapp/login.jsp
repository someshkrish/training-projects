<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Login | Zoho</title>
	</head>
	<body>
	    <form action='http://localhost/servletProject/api/v1/login' method='POST'>
	        User name: <input type="text" name="userName"/>
	        <br/><br/>
	        <input type="submit" value="Submit"/>
	    </form>
	    <p>${msg}</p>
	</body>
</html>
