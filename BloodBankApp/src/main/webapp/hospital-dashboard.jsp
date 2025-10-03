<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    // Security check: Ensures a valid hospital user is logged in.
    Hospital hospital = (Hospital) session.getAttribute("hospital");
    if (hospital == null) {
        response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
        return;
    }
    // Retrieve one-time messages for the toast notifications from the request parameters.
    String successMessage = request.getParameter("success");
    String errorMessage = request.getParameter("error");
%>
<html>
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>PLASMIC - Hospital Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            margin: 0;
            background: #f4f7f9;
            padding: 20px;
        }
        .container {
            max-width: 1400px;
            margin: 0 auto;
            background: #ffffff;
            padding: 20px 40px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: linear-gradient(90deg, #d9534f 0%, #c9302c 100%);
            color: white;
            padding: 20px 30px;
            margin: -20px -40px 30px -40px;
            border-radius: 15px 15px 0 0;
            flex-wrap: wrap;
            gap: 15px;
        }
        .header h2 { color: white; margin: 0; font-weight: 700; }
        .header a { color: white; text-decoration: none; font-weight: 600; padding: 8px 15px; border: 1px solid white; border-radius: 20px; transition: all 0.3s ease; }
        .header a:hover { background-color: white; color: #d9534f; }
        h3 { color: #d9534f; font-weight: 600; padding-bottom: 10px; border-bottom: 2px solid #f1f1f1; margin-top: 0; margin-bottom: 20px; display: flex; align-items: center; font-size: 1.25rem; }
        h3.stock-header::before { content: '🩸'; margin-right: 10px; font-size: 1.5rem; }
        h3.incoming-header::before { content: '🚚'; margin-right: 10px; font-size: 1.5rem; }
        h3.processing-header::before { content: '📋'; margin-right: 10px; font-size: 1.5rem; }
        .dashboard-layout { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 30px; }
        .data-table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        .data-table th, .data-table td { padding: 12px 10px; text-align: left; border-bottom: 1px solid #eee; font-size: 0.9rem;}
        .data-table th { background-color: #f8f9fa; font-weight: 600; color: #333; }
        .data-table tbody tr:hover { background-color: #fdf5f5; transition: background-color 0.2s; }
        .actions { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
        .btn { border: none; color: white; padding: 8px 15px; border-radius: 20px; text-decoration: none; cursor: pointer; font-family: 'Poppins', sans-serif; font-size: 14px; font-weight: 600; white-space: nowrap; transition: all 0.3s ease; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .btn:hover { transform: translateY(-2px); box-shadow: 0 4px 10px rgba(0,0,0,0.15); }
        .btn-approve { background: linear-gradient(45deg, #28a745, #218838); }
        .btn-decline { background: linear-gradient(45deg, #dc3545, #c82333); }
        .btn-call { background: linear-gradient(45deg, #007bff, #0056b3); }
        .btn-warning { background: linear-gradient(45deg, #ffc107, #e0a800); color: #212529; }
        .btn-prescreen { background: linear-gradient(45deg, #ffc107, #e0a800); color: #212529; }
        .panel { padding: 25px; border-radius: 10px; background-color: #fff; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #555;}
        .form-group select, .form-group input { width: 100%; padding: 12px; border: 1px solid #ccc; border-radius: 8px; font-size: 1rem; transition: border-color 0.2s; }
        .form-group select:focus, .form-group input:focus { border-color: #d9534f; outline: none; }
        .form-group button { width: 100%; padding: 12px; }
        .empty-state { color: #6c757d; font-style: italic; margin-top: 15px; text-align: center; padding: 20px; }
        hr { margin:25px 0; border: 0; border-top: 1px solid #eee; }


        @media (max-width: 1200px) { .dashboard-layout { grid-template-columns: 1fr 1fr; } }
        @media (max-width: 768px) { .dashboard-layout { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />
    <div class="container">
        <div class="header">
            <h2>PLASMIC Hospital Portal</h2>
            <a href="${pageContext.request.contextPath}/logout">Logout, <c:out value="${hospital.name}"/></a>
        </div>

        <div class="dashboard-layout">
            <div class="panel">
                <h3 class="stock-header">Stock & Transfers</h3>
                <h4>Current Cleared Stock</h4>
                <table class="data-table">
                    <thead><tr><th>Blood Group</th><th>Units</th></tr></thead>
                    <tbody>
                        <c:forEach var="entry" items="${currentStock}">
                            <tr>
                                <td><c:out value="${entry.key}"/></td>
                                <td><c:out value="${entry.value}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty currentStock}">
                            <tr><td colspan="2" class="empty-state">No cleared stock.</td></tr>
                        </c:if>
                    </tbody>
                </table>

                <hr>
                <h4>Emergency Donor Contacts</h4>
                 <p style="font-size: 0.8em; color: #666;">
                    This list shows available emergency donors for blood types currently out of stock.
                </p>
                <div id="emergency-contacts-container"></div>

                <hr>
                <h4>Manual Stock Adjustment</h4>
                <form action="${pageContext.request.contextPath}/manual-add-stock" method="post" style="margin-bottom: 20px;">
                    <div class="form-group">
                        <label>Blood Group:</label>
                        <select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select>
                    </div>
                    <div class="form-group">
                        <label>Units to Add:</label>
                        <input type="number" name="units" min="1" required>
                    </div>
                    <button type="submit" class="btn btn-approve">Add to Inventory</button>
                </form>

                <h4>Remove Damaged/Used Stock</h4>
                <form action="${pageContext.request.contextPath}/manual-remove-stock" method="post">
                    <div class="form-group">
                        <label>Blood Group:</label>
                        <select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select>
                    </div>
                    <div class="form-group">
                        <label>Units to Remove:</label>
                        <input type="number" name="units" min="1" required>
                    </div>
                    <button type="submit" class="btn btn-decline">Remove from Inventory</button>
                </form>

                <hr>
                <h4>Request Stock Transfer</h4>
                <form action="${pageContext.request.contextPath}/request-transfer" method="post">
                    <div class="form-group"><label>Request From:</label><select name="supplyingHospitalId" required><c:forEach var="h" items="${otherHospitals}"><option value="${h.id}">${h.name}</option></c:forEach></select></div>
                    <div class="form-group"><label>Blood Group:</label><select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select></div>
                    <div class="form-group"><label>Units:</label><input type="number" name="units" min="1" required></div>
                    <button type="submit" class="btn btn-call">Submit Request</button>
                </form>
            </div>
            <div class="panel">
                <h3 class="incoming-header">Incoming Queue</h3>
                <h4>Incoming Shipments (In-Transit)</h4>
                <c:if test="${empty inTransitBags}"><p class="empty-state">No shipments in transit.</p></c:if>
                <c:if test="${not empty inTransitBags}">
                    <table class="data-table">
                        <thead><tr><th>Bag ID</th><th>Blood Group</th><th>Action</th></tr></thead>
                        <tbody>
                            <c:forEach var="bag" items="${inTransitBags}">
                                <tr>
                                    <td>${bag.bagId}</td><td>${bag.bloodGroup}</td>
                                    <td class="actions"><a href="${pageContext.request.contextPath}/receive-transfer?bagId=${bag.bagId}" class="btn btn-approve">Receive</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <hr>
                <h4>Incoming Stock Transfer Requests</h4>
                <c:if test="${empty pendingTransfers}"><p class="empty-state">No incoming requests.</p></c:if>
                <c:if test="${not empty pendingTransfers}">
                    <table class="data-table">
                        <thead><tr><th>From</th><th>Group</th><th>Units</th><th>Action</th></tr></thead>
                        <tbody>
                            <c:forEach var="transfer" items="${pendingTransfers}">
                                <tr>
                                    <td>${transfer.requestingHospitalName}</td><td>${transfer.bloodGroup}</td><td>${transfer.units}</td>
                                    <td class="actions">
                                        <form action="${pageContext.request.contextPath}/approve-transfer" method="post" style="display:inline;">
                                            <input type="hidden" name="transferId" value="${transfer.transferId}">
                                            <input type="hidden" name="status" value="APPROVED">
                                            <button type="submit" class="btn btn-approve">Approve</button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/approve-transfer" method="post" style="display:inline;">
                                            <input type="hidden" name="transferId" value="${transfer.transferId}">
                                            <input type="hidden" name="status" value="DECLINED">
                                            <button type="submit" class="btn btn-decline">Decline</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
            <div class="panel">
                <h3 class="processing-header">Processing Queue</h3>
                
                <h4>Pending Patient Blood Requests</h4>
                <c:if test="${empty pendingRequests}"><p class="empty-state">No pending patient requests.</p></c:if>
                <c:if test="${not empty pendingRequests}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Patient</th>
                                <th>Group</th>
                                <th>Units</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="req" items="${pendingRequests}">
                                <tr>
                                    <td>${req.patientName}</td>
                                    <td>${req.bloodGroup}</td>
                                    <td>${req.units}</td>
                                    <td class="actions">
                                        <form action="${pageContext.request.contextPath}/approve-request" method="post" style="display:inline;">
                                            <input type="hidden" name="requestId" value="${req.requestId}">
                                            <button type="submit" class="btn btn-approve">Approve</button>
                                        </form>
                                        
                                        <c:if test="${req.units > (empty currentStock[req.bloodGroup] ? 0 : currentStock[req.bloodGroup])}">
                                            <button class="btn btn-warning" onclick="toggleDonorSelection('donor-row-${req.requestId}')">
                                                Via Donor
                                            </button>
                                        </c:if>
                                        
                                        <form action="${pageContext.request.contextPath}/decline-request" method="post" style="display:inline;">
                                            <input type="hidden" name="requestId" value="${req.requestId}">
                                            <button type="submit" class="btn btn-decline">Decline</button>
                                        </form>
                                    </td>
                                </tr>

                                <tr id="donor-row-${req.requestId}" class="donor-selection-row" style="display: none;">
                                    <td colspan="4" style="background-color: #fdf5f5; padding: 20px; border-bottom: 2px solid #d9534f;">
                                        <h5 style="margin-top: 0; color: #c9302c;">Select Emergency Donor(s) for ${req.patientName}</h5>
                                        <form action="${pageContext.request.contextPath}/fulfill-via-emergency" method="post">
                                            <input type="hidden" name="requestId" value="${req.requestId}">
                                            
                                            <c:set var="donorsForRequest" value="${emergencyContacts[req.bloodGroup]}" />
                                            <c:if test="${empty donorsForRequest}">
                                                <p class="empty-state">No available emergency donors for ${req.bloodGroup}.</p>
                                            </c:if>
                                            <c:if test="${not empty donorsForRequest}">
                                                <div class="donor-list" style="max-height: 150px; overflow-y: auto; border: 1px solid #eee; padding: 10px; border-radius: 5px; background: #fff;">
                                                    <c:forEach var="donor" items="${donorsForRequest}">
                                                        <label style="display: block; margin-bottom: 5px; cursor: pointer; padding: 5px; border-radius: 3px;" onmouseover="this.style.backgroundColor='#f0f0f0'" onmouseout="this.style.backgroundColor='transparent'">
                                                            <input type="checkbox" name="donorId" value="${donor.id}" style="margin-right: 8px;">
                                                            ${donor.name}
                                                        </label>
                                                    </c:forEach>
                                                </div>
                                                <button type="submit" class="btn btn-approve" style="margin-top: 15px;">Confirm Fulfillment</button>
                                            </c:if>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                <hr>
                <h4>Pending Donation Appointments</h4>
                <c:if test="${empty pendingDonations}"><p class="empty-state">No pending appointments.</p></c:if>
                <c:if test="${not empty pendingDonations}">
                    <table class="data-table">
                       <thead><tr><th>Donor</th><th>Status</th><th>Action</th></tr></thead>
                       <tbody>
                           <c:forEach var="appt" items="${pendingDonations}">
                               <tr>
                                   <td>${appt.donorName}</td><td>${appt.status.replace('_', ' ')}</td>
                                   <td class="actions">
                                       <c:if test="${appt.status == 'PENDING'}">
                                           <form action="update-donation-status" method="POST" style="display: inline;">
                                               <input type="hidden" name="donationId" value="${appt.donationId}">
                                               <input type="hidden" name="newStatus" value="PRE-SCREEN_PASSED">
                                               <button type="submit" class="btn btn-prescreen">Pass Screen</button>
                                           </form>
                                       </c:if>
                                       <c:if test="${appt.status == 'PRE-SCREEN_PASSED'}">
                                            <form action="approve-donation" method="POST" style="display: inline-block;">
                                                <input type="hidden" name="donationId" value="${appt.donationId}">
                                                <label for="donationDate-${appt.donationId}" style="font-weight: normal; font-size: 0.8em;">Date:</label>
                                                <input type="date" id="donationDate-${appt.donationId}" name="donationDate" value="<%= java.time.LocalDate.now() %>" required style="padding: 5px; width: auto; border-radius: 5px;">
                                                <button type="submit" class="btn btn-approve">Complete</button>
                                            </form>
                                       </c:if>
                                       <form action="update-donation-status" method="POST" style="display: inline;">
                                            <input type="hidden" name="donationId" value="${appt.donationId}">
                                            <input type="hidden" name="newStatus" value="CANCELLED">
                                            <button type="submit" class="btn btn-decline">Cancel</button>
                                       </form>
                                   </td>
                               </tr>
                           </c:forEach>
                       </tbody>
                    </table>
                </c:if>
                <hr>
                <h4>Pending Inventory (Awaiting Tests)</h4>
                <c:if test="${empty pendingBags}"><p class="empty-state">No bags awaiting tests.</p></c:if>
                <c:if test="${not empty pendingBags}">
                    <table class="data-table">
                       <thead><tr><th>Bag ID</th><th>Group</th><th>Donated On</th><th>Action</th></tr></thead>
                       <tbody>
                           <c:forEach var="bag" items="${pendingBags}">
                               <tr>
                                   <td>${bag.bagId}</td><td>${bag.bloodGroup}</td><td>${bag.dateDonated}</td>
                                   <td class="actions"><a href="${pageContext.request.contextPath}/update-inventory-status?bagId=${bag.bagId}&status=CLEARED" class="btn btn-approve">Clear</a></td>
                               </tr>
                           </c:forEach>
                       </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </div>

    <script>
        function toggleDonorSelection(rowId) {
            const donorRow = document.getElementById(rowId);
            if (donorRow) {
                document.querySelectorAll('.donor-selection-row').forEach(row => {
                    if (row.id !== rowId) {
                        row.style.display = 'none';
                    }
                });
                if (donorRow.style.display === 'none') {
                    donorRow.style.display = 'table-row';
                } else {
                    donorRow.style.display = 'none';
                }
            }
        }

        document.addEventListener('DOMContentLoaded', function() {
            let emergencyContacts = {};
            const emergencyContactsJson = '${emergencyContactsJson}';
            
            try {
                if (emergencyContactsJson) {
                    emergencyContacts = JSON.parse(emergencyContactsJson);
                }
            } catch (e) {
                console.error("Could not parse emergency contacts JSON:", e);
            }

            const container = document.getElementById('emergency-contacts-container');
            const contactKeys = Object.keys(emergencyContacts);

            if (contactKeys.length === 0) {
                container.innerHTML = '<p class="empty-state">No emergency contacts needed at this time.</p>';
            } else {
                // ✅ THE FIX: This new loop builds the HTML elements safely and correctly.
                contactKeys.forEach(bloodGroup => {
                    // Create the container for the group
                    const groupContainer = document.createElement('div');

                    // Create and style the title (e.g., <h5>Needed: AB+</h5>)
                    const title = document.createElement('h5');
                    title.style.color = '#c9302c';
                    title.style.marginTop = '20px';
                    title.textContent = `Needed: ${bloodGroup}`;
                    groupContainer.appendChild(title);

                    // Create the table and its header
                    const table = document.createElement('table');
                    table.className = 'data-table';
                    table.innerHTML = `<thead><tr><th>Name</th><th>Action</th></tr></thead>`;
                    
                    const tbody = document.createElement('tbody');
                    
                    // Get the list of donors for the current blood group
                    const donors = emergencyContacts[bloodGroup];
                    
                    donors.forEach(donor => {
                        const row = tbody.insertRow();
                        const nameCell = row.insertCell();
                        nameCell.textContent = donor.name;
                        
                        // Create the "Call Now" button safely
                        const actionCell = row.insertCell();
                        const callLink = document.createElement('a');
                        callLink.href = `tel:${donor.contactNumber || ''}`;
                        callLink.className = 'btn btn-call';
                        callLink.textContent = 'Call Now';
                        actionCell.appendChild(callLink);
                    });
                    
                    table.appendChild(tbody);
                    groupContainer.appendChild(table);
                    container.appendChild(groupContainer);
                });
            }
        });
    </script>
</body>
</html>