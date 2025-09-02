<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*, java.sql.Date, java.time.LocalDate" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User u = (User) session.getAttribute("user");
    if (u == null || !"DONOR".equals(u.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Determine eligibility for a standard donation
    boolean isEligible = true;
    if (u.getNextEligibleDate() != null && LocalDate.now().isBefore(u.getNextEligibleDate().toLocalDate())) {
        isEligible = false;
    }
    
    // Fetch data for the page
    Donation appointment = DonationDAO.getPendingAppointmentForDonor(u.getId());
    Date emergencyExpiry = EmergencyDonorDAO.getEmergencyStatusExpiry(u.getId());
    List<Hospital> hospitals = null;
    if (isEligible && appointment == null) {
        hospitals = HospitalDAO.getAllHospitals();
    }
    
    // ✅ ADDED: Check if the donor made a donation today (likely an emergency one)
    boolean hasDonatedToday = false;
    if (u.getLastDonationDate() != null && u.getLastDonationDate().toLocalDate().isEqual(LocalDate.now())) {
        hasDonatedToday = true;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>PLASMIC - Donor Dashboard</title>
    <style>
        body { font-family: 'Poppins', sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { max-width: 700px; margin: 40px auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; }
        h2, h3 { color: #c9302c; }
        .status-box { padding: 15px; border-radius: 5px; border-left: 5px solid; margin-bottom: 20px; }
        .eligible { background-color: #d4edda; border-color: #28a745; }
        .ineligible { background-color: #f8d7da; border-color: #dc3545; }
        .emergency-box { background-color: #fff3cd; border-left: 5px solid #ffc107; padding: 15px; margin-top: 30px; border-radius: 5px; }
        .emergency-box.thank-you { background-color: #d1ecf1; border-color: #007bff; } /* Blue for thank you */
        .emergency-box button { background-color: #dc3545; color: white; padding: 10px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }
    </style>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= u.getName() %> (Donor)</h2>
            <a href="logout">Logout</a>
        </div>
        
        <p>Your Blood Group: <strong><%= u.getBloodGroup() %></strong></p>

        <hr>

        <%-- Display a clear eligibility status box --%>
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

        <%-- Logic for showing appointment form or details --%>
        <% if (appointment != null) { %>
            <h3>Your Upcoming Appointment</h3>
            <p><strong>Date:</strong> <%= appointment.getAppointmentDate() %></p>
            <p><strong>Hospital:</strong> <%= appointment.getHospitalName() %></p>
        <% } else if (isEligible) { %>
            <h3>Request a Donation Appointment</h3>
            <form action="donate" method="post">
                <%-- Your appointment request form here --%>
            </form>
        <% } %>

        <%-- ✅ MODIFIED: This section now shows a thank you note if the donor donated today --%>
        <% if (hasDonatedToday) { %>
            <div class="emergency-box thank-you">
                <h3>Thank You for Your Donation!</h3>
                <p>Thank you for responding to an emergency request and donating today. You are a true hero!</p>
            </div>
        <% } else { %>
            <div class="emergency-box">
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
    </div>
</body>
</html>