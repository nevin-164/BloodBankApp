<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*, java.sql.Date, java.time.LocalDate, model.Achievement, model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User u = (User) session.getAttribute("user");
    if (u == null || !"DONOR".equals(u.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    boolean isEligible = true;
    if (u.getNextEligibleDate() != null && LocalDate.now().isBefore(u.getNextEligibleDate().toLocalDate())) {
        isEligible = false;
    }

    boolean hasDonatedToday = false;
    if (u.getLastDonationDate() != null && u.getLastDonationDate().toLocalDate().isEqual(LocalDate.now())) {
        hasDonatedToday = true;
    }

    Donation appointment = dao.DonationDAO.getPendingAppointmentForDonor(u.getId());
    Date emergencyExpiry = dao.EmergencyDonorDAO.getEmergencyStatusExpiry(u.getId());
    List<Hospital> hospitals = null;
    if (isEligible && appointment == null) {
        hospitals = dao.HospitalDAO.getAllHospitals();
    }
    
    List<Achievement> achievements = dao.AchievementDAO.getAchievementsForUser(u.getId());
    request.setAttribute("achievements", achievements);
    
    // ✅ FIXED: Using the single, correct method name from the final DAO
    List<Request> myRequests = dao.RequestDAO.getRequestsByUserId(u.getId());
    request.setAttribute("myRequests", myRequests);
%>
<!DOCTYPE html>
<html>
<head>
    <title>PLASMIC - Donor Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display.swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }
        body { font-family: 'Poppins', sans-serif; margin: 0; background-color: #f8f9fa; padding: 20px; }
        .container { max-width: 700px; width: 100%; margin: 0 auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; flex-wrap: wrap; gap: 15px; }
        h2, h3 { color: #c9302c; }
        h2 { margin: 0; }
        a { text-decoration: none; color: #c9302c; font-weight: 600; }
        .status-box { padding: 15px; border-radius: 5px; border-left: 5px solid; margin-bottom: 20px; }
        .eligible { background-color: #d4edda; border-color: #28a745; }
        .ineligible { background-color: #f8d7da; border-color: #dc3545; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; font-weight: 600; margin-bottom: 5px; }
        .form-group input, .form-group select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 5px; font-family: 'Poppins', sans-serif; font-size: 16px; }
        .form-group button { width: 100%; background-color: #28a745; color: white; padding: 12px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: 600; }
        .form-group button:hover { background-color: #218838; }
        .emergency-box { padding: 15px; border-radius: 5px; margin-top: 20px; }
        .emergency-box p { margin: 5px 0; }
        .emergency-box button { background-color: #c9302c; }
        .emergency-box button:hover { background-color: #a71d2a; }
        .thank-you { background-color: #d1ecf1; border-left: 5px solid #0c5460; }
        .thank-you h3 { color: #0c5460; }
        .achievements-container, .patient-request-container, .status-container {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px solid #eee;
        }
        .badge-list { display: flex; flex-wrap: wrap; gap: 15px; }
        .badge { display: flex; align-items: center; background: #f4f4f4; border-radius: 8px; padding: 10px; width: 100%; max-width: 250px; box-shadow: 0 1px 2px rgba(0,0,0,0.1); }
        .badge img { width: 40px; height: 40px; margin-right: 10px; }
        .badge-info h4 { margin: 0; font-size: 1rem; color: #333; }
        .badge-info p { margin: 0; font-size: 0.8rem; color: #777; }
        .patient-request-container button { background-color: #d9534f; }
        .patient-request-container button:hover { background-color: #c9302c; }
        .status-table { width: 100%; border-collapse: collapse; }
        .status-table th, .status-table td { border: 1px solid #ddd; padding: 8px 10px; text-align: left; }
        .status-table th { background-color: #f4f4f4; }
        .status-PENDING { font-weight: bold; color: #ffc107; }
        .status-FULFILLED { font-weight: bold; color: #28a745; }
        .status-DECLINED { font-weight: bold; color: #dc3545; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= u.getName() %></h2>
            <a href="logout">Logout</a>
        </div>
        
        <% if (isEligible) { %>
            <div class="status-box eligible">
                <strong>You are eligible to book a standard donation appointment.</strong>
            </div>
        <% } else { %>
            <div class="status-box ineligible">
                <strong>You are not yet eligible to book a standard donation.</strong><br>
                Your last donation was on: <%= u.getLastDonationDate() %><br>
                Your next eligible date is: <strong><%= u.getNextEligibleDate() %></strong>
            </div>
        <% } %>

        <% if (appointment != null) { %>
            <h3>Your Upcoming Appointment</h3>
            <p><strong>Date:</strong> <%= appointment.getAppointmentDate() %></p>
            <p><strong>Hospital:</strong> <%= appointment.getHospitalName() %></p>
        <% } else if (isEligible) { %>
            <h3>Request a Donation Appointment</h3>
            <form action="donate" method="post" class="appointment-form">
                <div class="form-group">
                    <label for="hospitalId">Choose a Hospital:</label>
                    <select id="hospitalId" name="hospitalId" required>
                         <% for (Hospital h : hospitals) { %>
                            <option value="<%= h.getId() %>"><%= h.getName() %></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="appointmentDate">Preferred Date:</label>
                    <input type="date" id="appointmentDate" name="appointmentDate" required min="<%= LocalDate.now().plusDays(1) %>" max="<%= LocalDate.now().plusDays(30) %>">
                </div>
                <div class="form-group">
                    <label for="units">Units to Donate:</label>
                    <input type="number" id="units" name="units" min="1" value="1" required>
                </div>
                <div class="form-group">
                    <button type="submit">Request Appointment</button>
                </div>
            </form>
        <% } %>

        <% if (hasDonatedToday) { %>
            <div class="emergency-box thank-you">
                <h3>Thank You for Your Donation!</h3>
                <p>Thank you for responding to an emergency request and donating today. You are a true hero!</p>
            </div>
        <% } else { %>
            <div class="emergency-box" style="background-color:#fff3cd; border-left:5px solid #856404;">
                <h3>Emergency Donor Program</h3>
                <% if (emergencyExpiry != null) { %>
                    <p>You are an active emergency donor. Thank you for your commitment!</p>
                    <p>Your availability expires on: <strong><%= emergencyExpiry %></strong></p>
                <% } else { %>
                    <p>Help save lives in critical situations. Sign up to be available for emergency calls for one week.</p>
                    <form action="emergency-signup" method="post" style="margin-top:10px;">
                        <button type="submit">Sign Up as Emergency Donor</button>
                    </form>
                <% } %>
            </div>
        <% } %>

        <div class="achievements-container">
            <h3>Your Achievements</h3>
            <c:if test="${empty achievements}">
                <p>Your earned badges will appear here. Go donate to earn your first!</p>
            </c:if>
            <c:if test="${not empty achievements}">
                <div class="badge-list">
                    <c:forEach var="ach" items="${achievements}">
                        <div class="badge">
                            <img src="${pageContext.request.contextPath}/${ach.badgeIcon}" alt="${ach.badgeName}">
                            <div class="badge-info">
                                <h4>${ach.badgeName}</h4>
                                <p>Earned on: ${ach.dateEarned}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
        
        <div class="patient-request-container">
            <h3>Request Blood</h3>
            <form action="request-blood" method="post">
                 <div class="form-group">
                    <label for="patientName">Patient Name:</label>
                    <input type="text" id="patientName" name="patientName" required>
                </div>
                <div class="form-group">
                    <label for="hospitalId_req">Choose a Hospital:</label>
                    <select id="hospitalId_req" name="hospitalId" required>
                         <% if (hospitals != null) { for (Hospital h : hospitals) { %>
                            <option value="<%= h.getId() %>"><%= h.getName() %></option>
                        <% }} %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="blood_group">Blood Group Needed:</label>
                    <select id="blood_group" name="bloodGroup" required>
                        <option value="A+">A+</option><option value="A-">A-</option><option value="B+">B+</option><option value="B-">B-</option><option value="AB+">AB+</option><option value="AB-">AB-</option><option value="O+">O+</option><option value="O-">O-</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="units_req">Units:</label>
                    <input type="number" id="units_req" name="units" min="1" value="1" required>
                </div>
                <button type="submit">Submit Request</button>
            </form>
            <c:if test="${not empty msg}">
                <p class="message ${msg.contains('submitted') ? 'success' : 'error'}">${msg}</p>
            </c:if>
        </div>
        
        <div class="status-container">
            <h3>My Request History</h3>
            <c:if test="${empty myRequests}">
                <p>You have no active or past blood requests.</p>
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
                                <%-- ✅ FIXED: Using the correct property names from the final Request model --%>
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

