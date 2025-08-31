<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, model.Hospital, model.Donation, dao.DonationDAO, dao.HospitalDAO, java.util.List, java.time.LocalDate" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User u = (User) session.getAttribute("user");
    if (u == null || !"DONOR".equals(u.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Determine eligibility based on the user object from the session
    boolean isEligible = true;
    if (u.getNextEligibleDate() != null && LocalDate.now().isBefore(u.getNextEligibleDate().toLocalDate())) {
        isEligible = false;
    }
    
    // Fetch data for the page
    Donation appointment = DonationDAO.getPendingAppointmentForDonor(u.getId());
    List<Hospital> hospitals = null;
    if (isEligible && appointment == null) {
        hospitals = HospitalDAO.getAllHospitals();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>PLASMIC - Donor Dashboard</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { max-width: 700px; margin: 40px auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; }
        h2, h3 { color: #c9302c; }
        .status-box { padding: 15px; border-radius: 5px; border-left: 5px solid; margin-bottom: 20px; }
        .eligible { background-color: #d4edda; border-color: #28a745; }
        .ineligible { background-color: #f8d7da; border-color: #dc3545; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= u.getName() %> (Donor)</h2>
            <a href="logout">Logout</a>
        </div>
        
        <hr>

        <%-- ✅ MODIFIED: Display a clear eligibility status box --%>
        <% if (isEligible) { %>
            <div class="status-box eligible">
                <strong>You are eligible to donate. Thank you for your continued support!</strong>
            </div>
        <% } else { %>
            <div class="status-box ineligible">
                <strong>You are not yet eligible to donate.</strong><br>
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
                <label for="hospital_id">Select Hospital:</label>
                <select name="hospital_id" id="hospital_id" required>
                    <% for (Hospital h : hospitals) { %>
                        <option value="<%= h.getId() %>"><%= h.getName() %></option>
                    <% } %>
                </select><br><br>
                <label for="units">Units to Donate:</label>
                <input type="number" id="units" name="units" min="1" value="1" required><br><br>
                <button type="submit">Request Appointment</button>
            </form>
        <% } %>

    </div>
</body>
</html>