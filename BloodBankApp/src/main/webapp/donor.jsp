<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*, java.sql.Date, java.time.LocalDate, model.Achievement" %> <%-- ✅ ADDED Achievement --%>
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
    if (u.getLastDonationDate() != null &&
        u.getLastDonationDate().toLocalDate().isEqual(LocalDate.now())) {
        hasDonatedToday = true;
    }

    Donation appointment = DonationDAO.getPendingAppointmentForDonor(u.getId());
    Date emergencyExpiry = EmergencyDonorDAO.getEmergencyStatusExpiry(u.getId());
    List<Hospital> hospitals = null;
    if (isEligible && appointment == null) {
        hospitals = HospitalDAO.getAllHospitals();
    }
    
    // --- ✅ NEW: Gamification Data (Phase 2) ---
    List<Achievement> achievements = dao.AchievementDAO.getAchievementsForUser(u.getId());
    request.setAttribute("achievements", achievements); // Set for JSTL to use
%>
<!DOCTYPE html>
<html>
<head>
    <title>PLASMIC - Donor Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
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

        /* Emergency donor section */
        .emergency-box { padding: 15px; border-radius: 5px; margin-top: 20px; }
        .emergency-box p { margin: 5px 0; }
        .emergency-box button { background-color: #c9302c; }
        .emergency-box button:hover { background-color: #a71d2a; }
        .thank-you { background-color: #d1ecf1; border-left: 5px solid #0c5460; }
        .thank-you h3 { color: #0c5460; }

        /* --- ✅ NEW: Achievement Styles --- */
        .achievements-container {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px solid #eee;
        }
        .badge-list {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
        }
        .badge {
            display: flex;
            align-items: center;
            background: #f4f4f4;
            border-radius: 8px;
            padding: 10px;
            width: 100%; /* Make badges full width on mobile */
            max-width: 250px; /* Max width on desktop */
            box-shadow: 0 1px 2px rgba(0,0,0,0.1);
        }
        .badge img {
            width: 40px;
            height: 40px;
            margin-right: 10px;
        }
        .badge-info h4 {
            margin: 0;
            font-size: 1rem;
            color: #333;
        }
        .badge-info p {
            margin: 0;
            font-size: 0.8rem;
            color: #777;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= u.getName() %> (Donor)</h2>
            <a href="logout">Logout</a>
        </div>
        
        <%-- Eligibility Status Box --%>
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

        <%-- Appointment Logic --%>
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

        <%-- Emergency Donor Section --%>
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

        <%-- ✅ NEW: Achievements Section (Phase 2) --%>
        <div class="achievements-container">
            <h3>Your Achievements</h3>
            
            <%-- Check if the achievements list from the scriptlet is empty --%>
            <c:if test="${empty achievements}">
                <p>Your earned badges will appear here. Go donate to earn your first!</p>
            </c:if>
            
            <c:if test="${not empty achievements}">
                <div class="badge-list">
                    <%-- Loop through the achievements and display them --%>
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
        
    </div> <%-- This is the closing .container div --%>
</body>
</html>