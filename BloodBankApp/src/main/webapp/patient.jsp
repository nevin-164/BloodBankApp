<%@ page import="model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User u = (User) session.getAttribute("user");
    if (u == null || !"PATIENT".equals(u.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Patient Dashboard</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { max-width: 600px; margin: 40px auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; }
        h2, h3 { color: #c9302c; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="number"], input[type="text"] { width: 100%; box-sizing: border-box; padding: 8px; border-radius: 4px; border: 1px solid #ccc; }
        input[readonly] { background-color: #e9ecef; }
        button { background-color: #d9534f; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; width: 100%; font-size: 16px; }
        .message { padding: 10px; margin-top: 20px; border-radius: 4px; text-align: center; font-weight: bold; }
        .success { background-color: #dff0d8; color: #3c763d; }
        .error { background-color: #f2dede; color: #a94442; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= u.getName() %> (Patient)</h2>
            <a href="logout">Logout</a>
        </div>

        <h3>Request Blood</h3>
        <form action="request-blood" method="post">
            <div class="form-group">
                <label>Blood Group Needed:</label>
                <%-- The patient's blood group is used automatically on the server --%>
                <input type="text" value="<%= u.getBloodGroup() %>" readonly>
            </div>
            <div class="form-group">
                <label for="units">Units:</label>
                <input type="number" id="units" name="units" min="1" value="1" required>
            </div>
            <button type="submit">Request Blood</button>
        </form>

        <%-- Display messages from the servlet --%>
        <c:if test="${not empty msg}">
            <p class="message ${msg.contains('fulfilled') ? 'success' : 'error'}">${msg}</p>
        </c:if>
    </div>
</body>
</html>