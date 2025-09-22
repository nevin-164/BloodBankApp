<%@ page import="model.User, model.Request, java.util.List, dao.RequestDAO" %> <%-- ✅ ADDED Request, List, RequestDAO --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User u = (User) session.getAttribute("user");
    if (u == null || !"PATIENT".equals(u.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // --- ✅ NEW: Real-Time Tracking Data (Phase 2) ---
    // Get the list of this patient's requests using the new DAO method
    List<Request> myRequests = dao.RequestDAO.getRequestsByPatientId(u.getId());
    request.setAttribute("myRequests", myRequests); // Set for JSTL to use
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

        /* --- ✅ NEW: Request Status Table Styles --- */
        .status-container {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px solid #eee;
        }
        .status-table {
            width: 100%;
            border-collapse: collapse;
        }
        .status-table th, .status-table td {
            border: 1px solid #ddd;
            padding: 8px 10px;
            text-align: left;
        }
        .status-table th {
            background-color: #f4f4f4;
        }
        /* Style for the status text */
        .status-Pending {
            font-weight: bold;
            color: #ffc107; /* Yellow */
        }
        .status-Approved {
            font-weight: bold;
            color: #28a745; /* Green */
        }
        .status-Completed {
            font-weight: bold;
            color: #007bff; /* Blue */
        }
        .status-Declined {
            font-weight: bold;
            color: #dc3545; /* Red */
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
        
        
        <%-- ✅ NEW: Real-Time Tracking Section (Phase 2) --%>
        <div class="status-container">
            <h3>My Request Status</h3>
            
            <c:if test="${empty myRequests}">
                <p>You have no active or past blood requests.</p>
            </c:if>
            
            <c:if test="${not empty myRequests}">
                <table class="status-table">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Blood Type</th>
                            <th>Units</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="req" items="${myRequests}">
                            <tr>
                                <td>${req.requestDate}</td>
                                <td>${req.bloodGroup}</td>
                                <td>${req.units}</td>
                                <%-- This displays the new tracking_status, with a matching CSS class --%>
                                <td class="status-${req.trackingStatus}">${req.trackingStatus}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
        
    </div>
</body>
</html>