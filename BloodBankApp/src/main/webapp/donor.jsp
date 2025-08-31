<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- ✅ FIXED: Added missing import for HospitalDAO --%>
<%@ page import="model.User, model.Hospital, model.Donation, dao.DonationDAO, dao.HospitalDAO, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User u = (User) session.getAttribute("user");
    if (u == null || !"DONOR".equals(u.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Check for any new notifications for the donor
    Donation notification = DonationDAO.getLatestDonationUpdateForDonor(u.getId());
    
    List<Hospital> hospitals = null;
    Donation appointment = null;
    // Only check for appointments/hospitals if there is no active notification
    if (notification == null) {
        appointment = DonationDAO.getPendingAppointmentForDonor(u.getId());
        if (appointment == null) {
            hospitals = HospitalDAO.getAllHospitals();
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Donor Dashboard</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { max-width: 700px; margin: 40px auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; }
        h2, h3 { color: #c9302c; }
        .notification { padding: 15px; border-radius: 5px; border-left: 5px solid; margin-bottom: 20px; }
        .approved { background-color: #d4edda; border-color: #28a745; }
        .declined { background-color: #f8d7da; border-color: #dc3545; }
        .notification a { background-color: #6c757d; color: white; padding: 5px 10px; border-radius: 4px; text-decoration: none; float: right; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= u.getName() %> (Donor)</h2>
            <a href="logout">Logout</a>
        </div>

        <%-- Notification Display Logic --%>
        <% if (notification != null) { %>
            <% if ("APPROVED".equals(notification.getStatus())) { %>
                <div class="notification approved">
                    <a href="clear-notification?donationId=<%= notification.getDonationId() %>&status=APPROVED">Clear</a>
                    <strong>Appointment Approved!</strong><br>
                    Your donation appointment at <strong><%= notification.getHospitalName() %></strong> has been approved. Thank you!
                </div>
            <% } else if ("DECLINED".equals(notification.getStatus())) { %>
                <div class="notification declined">
                    <a href="clear-notification?donationId=<%= notification.getDonationId() %>&status=DECLINED">Clear</a>
                    <strong>Appointment Declined.</strong><br>
                    Unfortunately, your recent appointment request at <strong><%= notification.getHospitalName() %></strong> was declined.
                </div>
            <% } %>
        <% } %>

        <%-- Existing logic for showing appointment or request form --%>
        <% if (appointment != null) { %>
            <h3>Your Upcoming Appointment</h3>
            <p><strong>Date:</strong> <%= appointment.getAppointmentDate() %></p>
            <p><strong>Hospital:</strong> <%= appointment.getHospitalName() %></p>
        <% } else if (hospitals != null) { %>
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