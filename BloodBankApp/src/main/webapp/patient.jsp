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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* ✅ NEW: Universal box-sizing and a more modern font stack */
        * {
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            background-color: #f8f9fa;
            padding: 20px; /* ✅ NEW: Add padding to body for spacing on mobile */
        }

        /* ✅ UPDATED: Container is now fluid */
        .container {
            max-width: 600px;
            width: 100%; /* Take full width of its parent (the padded body) */
            margin: 0 auto; /* Center horizontally, remove fixed vertical margin */
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap; /* Allows header items to wrap on very small screens */
            gap: 10px;
        }

        h2, h3 {
            color: #c9302c;
            margin-top: 0;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        input[type="number"], select {
            width: 100%;
            padding: 10px; /* Slightly larger padding for better touch targets */
            border-radius: 4px;
            border: 1px solid #ccc;
            font-size: 16px; /* Prevents iOS auto-zoom on focus */
        }
        
        button {
            background-color: #d9534f;
            color: white;
            padding: 12px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
            font-size: 16px;
            transition: background-color 0.2s;
        }

        button:hover {
            background-color: #c9302c;
        }
        
        .message {
            padding: 10px;
            margin-top: 20px;
            border-radius: 4px;
            text-align: center;
            font-weight: bold;
        }
        
        .success {
            background-color: #dff0d8;
            color: #3c763d;
        }
        
        .error {
            background-color: #f2dede;
            color: #a94442;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= u.getName() %></h2>
            <a href="logout">Logout</a>
        </div>

        <h3>Request Blood</h3>
        <form action="request-blood" method="post">
            
            <div class="form-group">
                <label for="blood_group">Blood Group Needed:</label>
                <select id="blood_group" name="blood_group" required>
                    <option value="A+">A+</option>
                    <option value="A-">A-</option>
                    <option value="B+">B+</option>
                    <option value="B-">B-</option>
                    <option value="AB+">AB+</option>
                    <option value="AB-">AB-</option>
                    <option value="O+">O+</option>
                    <option value="O-">O-</option>
                </select>
            </div>

            <div class="form-group">
                <label for="units">Units:</label>
                <input type="number" id="units" name="units" min="1" value="1" required>
            </div>
            <button type="submit">Submit Request</button>
        </form>

        <c:if test="${not empty msg}">
            <p class="message ${msg.contains('submitted') ? 'success' : 'error'}">${msg}</p>
        </c:if>
    </div>
</body>
</html>