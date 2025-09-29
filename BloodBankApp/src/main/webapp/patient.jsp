<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, dao.RequestDAO, model.Request, java.util.List, dao.HospitalDAO, model.Hospital" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User u = (User) session.getAttribute("user");
    // ✅ FIXED: Restored the proper role-based security check.
    if (u == null || !"PATIENT".equals(u.getRole())) { 
        response.sendRedirect("login.jsp");
        return;
    }
    // ✅ FIXED: Using the single, correct method name from the final DAO
    List<Request> myRequests = RequestDAO.getRequestsByUserId(u.getId());
    request.setAttribute("myRequests", myRequests);
    
    // Fetch all hospitals for the "Receiving Hospital" dropdown in the form
    List<Hospital> hospitals = HospitalDAO.getAllHospitals();
    request.setAttribute("hospitals", hospitals);
%>
<!DOCTYPE html>
<html>
<head>
    <title>PLASMIC - Patient Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display.swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }
        body { font-family: 'Poppins', sans-serif; margin: 0; background-color: #f8f9fa; padding: 20px; }
        .container { max-width: 800px; width: 100%; margin: 0 auto; background: #fff; padding: 20px 30px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 15px; margin-bottom: 20px; flex-wrap: wrap; gap: 15px; }
        h2, h3 { color: #c9302c; }
        h2 { margin: 0; }
        a { text-decoration: none; color: #c9302c; font-weight: 600; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; font-weight: 600; margin-bottom: 5px; }
        .form-group input, .form-group select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 5px; font-family: 'Poppins', sans-serif; font-size: 16px; }
        .form-group button { width: 100%; background-color: #d9534f; color: white; padding: 12px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: 600; transition: background-color 0.2s; }
        .form-group button:hover { background-color: #c9302c; }
        .status-container { margin-top: 30px; padding-top: 20px; border-top: 2px solid #eee; }
        .status-table { width: 100%; border-collapse: collapse; }
        .status-table th, .status-table td { border: 1px solid #ddd; padding: 12px 10px; text-align: left; }
        .status-table th { background-color: #f4f4f4; font-weight: 600; }
        .status-PENDING { font-weight: bold; color: #ffc107; }
        .status-FULFILLED { font-weight: bold; color: #28a745; }
        .status-DECLINED { font-weight: bold; color: #dc3545; }
        .empty-state { color: #6c757d; font-style: italic; text-align: center; padding: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= u.getName() %></h2>
            <a href="logout">Logout</a>
        </div>

        <h3>Request Blood For a Patient</h3>
        <form action="request-blood" method="post">
            <%-- ✅ FIXED: Added all necessary fields for a complete request --%>
            <div class="form-group">
                <label for="patientName">Patient's Full Name:</label>
                <input type="text" id="patientName" name="patientName" required>
            </div>
             <div class="form-group">
                <label for="hospitalId">Receiving Hospital:</label>
                <select id="hospitalId" name="hospitalId" required>
                    <c:forEach var="h" items="${hospitals}">
                        <option value="${h.id}">${h.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="bloodGroup">Blood Group Needed:</label>
                <select id="bloodGroup" name="bloodGroup" required>
                    <option value="A+">A+</option><option value="A-">A-</option><option value="B+">B+</option><option value="B-">B-</option><option value="AB+">AB+</option><option value="AB-">AB-</option><option value="O+">O+</option><option value="O-">O-</option>
                </select>
            </div>
            <div class="form-group">
                <label for="units">Units Required:</label>
                <input type="number" id="units" name="units" min="1" required>
            </div>
            <div class="form-group">
                <button type="submit">Submit Blood Request</button>
            </div>
        </form>

        <div class="status-container">
            <h3>My Request History</h3>
            <c:if test="${empty myRequests}">
                <p class="empty-state">You have no active or past blood requests.</p>
            </c:if>
            <c:if test="${not empty myRequests}">
                <table class="status-table">
                    <thead>
                        <tr>
                            <th>Request Date</th>
                            <th>Blood Type</th>
                            <th>Units</th>
                            <th>Hospital</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="req" items="${myRequests}">
                            <tr>
                                <td>${req.createdAt}</td>
                                <td>${req.bloodGroup}</td>
                                <td>${req.units}</td>
                                <td>${req.hospitalName != null ? req.hospitalName : 'N/A'}</td>
                                <td class="status-${req.status}">${req.status}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>
</body>
</html>

