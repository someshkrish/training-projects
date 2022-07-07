<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Home | Zoho</title>
	</head>
	<body>
	    
	    <div class="navigation-bar">
	        <div class="left-block">
	            <a href="">Link-one</a>
	            <a href="">Link-two</a>
	            <a href="">Link-three</a>
	            <a href="">Link-four</a>
	        </div>
	        <form class="right-block" action="http://localhost/servletProject/api/v1/logout" method="GET">
	          <input type="submit" value="Log Out">
	        </form>
	    </div>
	    
	    <hr>
	    
	    <div class="page-body">
	        <br/><br/>
	        <p class="result">Id: ${id}</p>
	        <p class="result">Name: ${name}</p>
	        <p class="result">Details: ${details}</p>
	        <br/><br/>
	    </div>
	</body>
	
	<style>
	    .navigation-bar{
	        padding: 1rem;
	        display: flex;
	        justify-content: space-between;
	    }
	    
	    .navigation-bar > .left-block {
	        width: 50vw;
	        display: flex;
	        justify-content: flex-start;
	    }
	    
	    .navigation-bar > .left-block > a{
	        margin: 0.5rem;
	    }
	    
	    .page-body{
	        padding: 1rem;
	    }
    </style>
     
</html>

