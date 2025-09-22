<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*, java.util.Set, java.util.HashSet, model.BloodInventory" %> <%-- ✅ ADDED BloodInventory --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // The main security check and all data loading is now done in the HospitalDashboardServlet.
    Hospital hospital = (Hospital) session.getAttribute("hospital");
    
    if (hospital == null) {
        // If no hospital in session, redirect to login
        response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
        return; // Stop the page from processing
    }
    // All data loading (StockDAO, RequestDAO, etc.) is handled by the HospitalDashboardServlet.
%>
<html>
<head>
    <title>PLASMIC - Hospital Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* --- Base & Desktop Styles --- */
        * { box-sizing: border-box; }
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol"; margin: 0; padding: 20px; background-color: #f9f9f9; }
        .container { max-width: 1200px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; flex-wrap: wrap; gap: 10px; }
        h2, h3 { color: #c9302c; }
        h2 { margin: 0; }
        .dashboard-layout { display: grid; grid-template-columns: 300px 1fr; gap: 40px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #f8f8f8; }
        .actions-cell, .emergency-donor-item { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
        .emergency-donor-item { justify-content: space-between; }
        .message { padding: 10px; margin: 20px 0; border-radius: 4px; text-align: center; font-weight: bold; }
        .success { background-color: #dff0d8; color: #3c763d; }
        .error { background-color: #f2dede; color: #a94442; }
        a.btn, button.btn { border: none; color: white; padding: 5px 10px; border-radius: 4px; text-decoration: none; cursor: pointer; font-family: sans-serif; font-size: 14px; white-space: nowrap; }
        .approve-btn { background-color: #28a745; }
        .decline-btn { background-color: #dc3545; }
        .call-btn { background-color: #007bff; }
        .emergency-donors { margin-top: 15px; padding: 10px; background-color: #fff3cd; border-left: 5px solid #ffeeba; border-radius: 5px; }

        /* ✅ UPDATED: Added new panel style */
        .analytics-panel, .pending-inventory-panel, .donation-management { 
            margin-top: 30px; 
            padding-top: 20px; 
            border-top: 2px solid #eee;
        }

        .stock-management-form { margin-top: 30px; padding-top: 20px; border-top: 2px solid #eee; }
        .stock-management-form .form-group { margin-bottom: 10px; }
        .stock-management-form label { display: block; margin-bottom: 5px; font-weight: bold; }
        .stock-management-form select, .stock-management-form input { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        .stock-management-form button { width: 100%; padding: 10px; background-color: #17a2b8; }

        /* --- Media Query for Mobile Devices --- */
        @media (max-width: 900px) {
            body { padding: 10px; }
            .dashboard-layout {
                grid-template-columns: 1fr; /* Stack the two main columns */
            }
            .header {
                flex-direction: column;
                align-items: flex-start;
            }

            /* Responsive Table Styling */
            table, thead, tbody, th, td, tr {
                display: block; /* Make table elements behave like blocks */
            }
            thead tr {
                position: absolute; /* Hide the original table headers */
                top: -9999px;
                left: -9999px;
            }
            tr {
                border: 1px solid #ccc;
                margin-bottom: 15px;
                border-radius: 5px;
                padding: 10px;
            }
            td {
                border: none;
                border-bottom: 1px solid #eee;
                position: relative;
                padding-left: 50%; /* Create space for the data labels */
                text-align: right; /* Align cell content to the right */
                min-height: 28px; /* Ensure consistent height */
            }
            td:last-child {
                border-bottom: 0;
            }
            td:before { /* Add data labels */
                content: attr(data-label);
                position: absolute;
                left: 10px;
                width: 45%;
                padding-right: 10px;
                white-space: nowrap;
                text-align: left;
                font-weight: bold;
            }
            
            /* Reset styles for action cells which don't need labels */
            td.actions-cell, .requests-management td:nth-of-type(4) {
                 padding-left: 0;
                 text-align: left;
            }
            td.actions-cell:before, .requests-management td:nth-of-type(4):before {
                content: ""; /* Remove the pseudo-label for action cells */
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= hospital.getName() %></h2>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        
        <c:if test="${not empty param.success}"><p class="message success">${param.success.replace('+', ' ')}</p></c:if>
        <c:if test="${not empty param.error}"><p class="message error">${param.error.replace('+', ' ')}</p></c:if>

        <div class="dashboard-layout">
            <div class="stock-display">
                <h3>Current Stock</h3>
                <table>
                    <thead><tr><th>Blood Group</th><th>Units</th></tr></thead>
                    <tbody>
                        <c:forEach var="entry" items="${currentStock}">
                            <tr>
                                <td data-label="Blood Group">${entry.key}</td>
                                <td data-label="Units">${entry.value}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <div class="analytics-panel">
                    <h3>Daily Analytics (Avg.)</h3>
                    <c:if test="${empty allBloodGroups}">
                        <p>No analytics data available yet.</p>
                    </c:if>
                    <c:if test="${not empty allBloodGroups}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Blood Group</th>
                                    <th>Avg. Daily Donations</th>
                                    <th>Avg. Daily Requests</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="bloodGroup" items="${allBloodGroups}">
                                    <tr>
                                        <td data-label="Blood Group">${bloodGroup}</td>
                                        <%
                                            Map<String, Double> donationsMap = (Map<String, Double>) request.getAttribute("avgDonations");
                                            Map<String, Double> requestsMap = (Map<String, Double>) request.getAttribute("avgRequests");
                                            String bg = (String) pageContext.getAttribute("bloodGroup");
                                            String avgDonText = String.format("%.1f", donationsMap.getOrDefault(bg, 0.0));
                                            String avgReqText = String.format("%.1f", requestsMap.getOrDefault(bg, 0.0));
                                        %>
                                        <td data-label="Avg. Donations"><%= avgDonText %></td>
                                        <td data-label="Avg. Requests"><%= avgReqText %></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </div>
                
                <div class="stock-management-form">
                    <h3>Manual Stock Management</h3>
                    <form action="${pageContext.request.contextPath}/manage-stock" method="post">
                        <div class="form-group"><label>Action:</label><select name="action" required><option value="add">Add</option><option value="remove">Remove</option><option value="set">Set Total</option></select></div>
                        <div class="form-group"><label>Blood Group:</label><select name="bloodGroup" required><option value="A+">A+</option><option value="A-">A-</option><option value="B+">B+</option><option value="B-">B-</option><option value="AB+">AB+</option><option value="AB-">AB-</option><option value="O+">O+</option><option value="O-">O-</option></select></div>
                        <div class="form-group"><label>Units:</label><input type="number" name="units" min="0" required></div>
                        <button type="submit" class="btn">Update Stock</button>
                    </form>
                </div>
            </div>

            <div class="management-panels">
                <div class="requests-management">
                    <h3>Pending Blood Requests</h3>
                    <c:if test="${empty pendingRequests}"><p>No pending blood requests.</p></c:if>
                    <c:if test="${not empty pendingRequests}">
                        <table>
                           <thead><tr><th>Patient</th><th>Blood Group</th><th>Units</th><th>Actions</th></tr></thead>
                           <tbody>
                               <c:forEach var="req" items="${pendingRequests}">
                                   <tr>
                                       <td data-label="Patient">${req.patientName}</td>
                                       <td data-label="Blood Group">${req.bloodGroup}</td>
                                       <td data-label="Units">${req.units}</td>
                                       <td data-label="Actions">
                                           <% 
                                               boolean stockAvailable = StockDAO.isStockAvailable(hospital.getId(), ((Request)pageContext.getAttribute("req")).getBloodGroup(), ((Request)pageContext.getAttribute("req")).getUnits());
                                               if (stockAvailable) {
                                           %>
                                               <a href="approve-request?requestId=${req.requestId}" class="btn approve-btn">Approve from Stock</a>
                                           <% } else { %>
                                               <div class="emergency-donors">
                                                   <strong>Insufficient Stock. Emergency Donors:</strong>
                                                   <% 
                                                       List<User> emergencyDonors = EmergencyDonorDAO.getAvailableEmergencyDonors(((Request)pageContext.getAttribute("req")).getBloodGroup());
                                                       pageContext.setAttribute("emergencyDonors", emergencyDonors);
                                                   %>
                                                   <c:if test="${empty emergencyDonors}"><p>None available.</p></c:if>
                                                   <c:forEach var="ed" items="${emergencyDonors}">
                                                        <div class="emergency-donor-item">
                                                            <span>${ed.name} (<button class="btn call-btn" onclick="callPatient('${ed.name}', '${ed.contactNumber}')">Call</button>)</span>
                                                            <a href="fulfill-via-emergency?requestId=${req.requestId}&donorId=${ed.id}" 
                                                               class="btn approve-btn"
                                                               onclick="return confirm('Confirm fulfillment? This will update their eligibility.');">Fulfill</a>
                                                        </div>
                                                   </c:forEach>
                                               </div>
                                           <% } %>
                                           <a href="decline-request?requestId=${req.requestId}" class="btn decline-btn">Decline</a>
                                       </td>
                                   </tr>
                               </c:forEach>
                           </tbody>
                        </table>
                    </c:if>
                </div>

                <div class="donation-management">
                    <h3>Pending Donation Appointments</h3>
                    <c:if test="${empty pendingDonations}"><p>No pending donation appointments.</p></c:if>
                    <c:if test="${not empty pendingDonations}">
                        <table>
                           <thead><tr><th>Donor</th><th>Blood Group</th><th>Units</th><th>Date</th><th>Action</th></tr></thead>
                           <tbody>
                               <c:forEach var="appt" items="${pendingDonations}">
                                   <tr>
                                       <td data-label="Donor">${appt.donorName}</td>
                                       <td data-label="Blood Group">${appt.bloodGroup}</td>
                                       <td data-label="Units">${appt.units}</td>
                                       <td data-label="Date">${appt.appointmentDate}</td>
                                       <td class="actions-cell" data-label="Action">
                                           <a href="approve-donation?donationId=${appt.donationId}" class="btn approve-btn" onclick="return confirm('Approve donation?');">Approve</a>
                                           <a href="decline-donation?donationId=${appt.donationId}" class="btn decline-btn" onclick="return confirm('Decline appointment?');">Decline</a>
                                       </td>
                                   </tr>
                               </c:forEach>
                           </tbody>
                        </table>
                    </c:if>
                </div>
                
                <%-- ✅ NEW: Pending Inventory Panel (Phase 4) --%>
                <div class="pending-inventory-panel">
                    <h3>Pending Inventory (Awaiting Lab Tests)</h3>
                    <c:if test="${empty pendingBags}">
                        <p>No bags are currently awaiting testing.</p>
                    </c:if>
                    <c:if test="${not empty pendingBags}">
                        <table>
                           <thead>
                               <tr>
                                   <th>Bag ID</th>
                                   <th>Blood Group</th>
                                   <th>Donation Date</th>
                                   <th>Expiry Date</th>
                                   <th>Action</th>
                               </tr>
                           </thead>
                           <tbody>
                               <c:forEach var="bag" items="${pendingBags}">
                                   <tr>
                                       <td data-label="Bag ID">${bag.bagId}</td>
                                       <td data-label="Blood Group">${bag.bloodGroup}</td>
                                       <td data-label="Donation Date">${bag.dateDonated}</td>
                                       <td data-label="Expiry Date">${bag.expiryDate}</td>
                                       <td class="actions-cell" data-label="Action">
                                           <a href="${pageContext.request.contextPath}/update-inventory-status?bagId=${bag.bagId}&status=CLEARED" 
                                              class="btn approve-btn"
                                              onclick="return confirm('Are you sure you want to clear this bag for use?');">Clear for Use</a>
                                       </td>
                                   </tr>
                               </c:forEach>
                           </tbody>
                        </table>
                    </c:if>
                </div>

            </div>
        </div>
    </div>
    <script>
        function callPatient(name, phone) {
            if (phone && phone !== 'null' && phone.trim() !== '') {
                alert("Please contact the patient:\n\nName: " + name + "\nPhone: " + phone);
            } else {
                alert("No contact number is available for patient: " + name);
            }
        }
    </script>
</body>
</html>