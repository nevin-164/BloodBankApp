<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<h1>User Registration</h1>

<% if (msg != null) { %>
    <p style="color:green;"><%= msg %></p>
<% } %>

<form action="<%= request.getContextPath() %>/register" method="post">

    <label>Full Name:</label>
    <input type="text" name="name" required><br>

    <label>Email:</label>
    <input type="email" name="email" required><br>

    <label>Password:</label>
    <input type="password" name="password" required><br>

    <label>Role:</label>
    <select name="role" required>
        <option value="donor">Donor</option>
        <option value="patient">Patient</option>
        <!-- Hospital role is intentionally excluded -->
    </select><br>

    <label>Blood Group (if donor/patient):</label>
    <input type="text" name="blood_group"><br>

    <input type="submit" value="Register">
</form>

<p>Already have an account? <a href="login.jsp">Login here</a></p>

</body>
</html>
