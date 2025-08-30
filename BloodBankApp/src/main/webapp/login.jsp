<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <title>User Login</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<h1>User Login</h1>

<% if (msg != null) { %>
    <p style="color:red;"><%= msg %></p>
<% } %>

<form action="<%= request.getContextPath() %>/login" method="post">

    <label>Email:</label>
    <input type="email" name="email" required><br>

    <label>Password:</label>
    <input type="password" name="password" required><br>

    <label>Role:</label>
    <select name="role" required>
        <option value="donor">Donor</option>
        <option value="patient">Patient</option>
        <option value="hospital">Hospital</option>
        <option value="admin">Admin</option>
    </select><br>

    <input type="submit" value="Login">
</form>

<p>Don’t have an account? <a href="register.jsp">Register here</a></p>

</body>
</html>
