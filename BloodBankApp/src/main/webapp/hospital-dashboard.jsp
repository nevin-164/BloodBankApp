<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    // Security check remains the same
    Hospital hospital = (Hospital) session.getAttribute("hospital");
    if (hospital == null) {
        response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
        return;
    }
    // Message retrieval remains the same
    String successMessage = request.getParameter("success");
    String errorMessage = request.getParameter("error");
%>
<html>
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>PLASMIC - Hospital Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            margin: 0;
            background-color: #f4f7fa;
            color: #444;
        }
        .container {
            display: flex;
            padding: 20px;
            gap: 20px;
            flex-wrap: wrap; /* Allows sidebar to wrap on smaller screens */
        }
        .main-dashboard {
            flex-grow: 1;
            min-width: 60%; /* Ensures main content has enough space */
        }
        .right-sidebar {
            width: 100%; /* Full width on small screens */
            max-width: 380px; /* Max width on large screens */
            flex-shrink: 0;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .header h2 {
            margin: 0;
            font-size: 28px;
            font-weight: 700;
        }
        .header .user-info {
            font-weight: 500;
        }
        .header .user-info a {
            color: #d9534f;
            text-decoration: none;
            font-weight: 600;
        }

        .stat-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }
        .stat-card {
            background: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.05);
            display: flex;
            align-items: center;
            gap: 15px;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }
        .stat-card .icon {
            font-size: 24px;
            color: #fff;
            background: #d9534f;
            height: 50px;
            width: 50px;
            border-radius: 50%;
            display: grid;
            place-items: center;
        }
        .stat-card .info h4 {
            margin: 0;
            font-size: 16px;
            color: #666;
        }
        .stat-card .info p {
            margin: 0;
            font-size: 24px;
            font-weight: 700;
        }

        .panel {
            background: rgba(255, 255, 255, 0.7);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(0, 0, 0, 0.05);
            border-radius: 15px;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.08);
            margin-bottom: 20px;
        }
        .panel-header {
            padding: 20px;
            border-bottom: 1px solid rgba(0,0,0,0.05);
            background: linear-gradient(90deg, rgba(217, 83, 79, 0.08) 0%, rgba(217, 83, 79, 0) 100%);
            border-radius: 15px 15px 0 0;
        }
        .panel-header h3 {
            margin: 0;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 10px;
            color: #d9534f;
        }
        .panel-body {
            padding: 20px;
        }
        
        .blood-stock-chart {
            display: flex;
            gap: 10px;
            align-items: flex-end;
            padding: 20px 0;
            min-height: 150px;
        }
        .blood-group-bar {
            flex-grow: 1;
            text-align: center;
        }
        .blood-group-bar .bar {
            background: #d9534f;
            border-radius: 5px 5px 0 0;
            width: 80%;
            margin: 0 auto;
            transition: height 0.5s ease-out;
            position: relative;
        }
         .blood-group-bar .bar .unit-count {
            position: absolute;
            top: -20px;
            left: 50%;
            transform: translateX(-50%);
            font-size: 12px;
            font-weight: 600;
            color: #333;
        }
        .blood-group-bar .label {
            font-weight: 600;
            margin-top: 5px;
            font-size: 14px;
        }
        
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th, .data-table td { padding: 12px 10px; text-align: left; border-bottom: 1px solid #eee; font-size: 14px; }
        .data-table th { font-weight: 600; color: #555; }
        .data-table .actions { display: flex; gap: 8px; flex-wrap: wrap; }
        
        .btn {
            border: none;
            color: white;
            padding: 8px 15px;
            border-radius: 8px;
            text-decoration: none;
            cursor: pointer;
            font-family: 'Poppins', sans-serif;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.2s ease;
        }
        .btn:hover { transform: translateY(-2px); box-shadow: 0 6px 15px rgba(0,0,0,0.15); }
        .btn-approve { background: #28a745; }
        .btn-decline { background: #dc3545; }
        .btn-call { background: #007bff; }
        .btn-warning { background: #ffc107; color: #212529; }
        .btn-prescreen { background: #17a2b8; }
        
        .form-group label { display: block; margin-bottom: 8px; font-weight: 500; color: #555;}
        .form-group select, .form-group input { width: 100%; padding: 12px; border: 1px solid #ccc; border-radius: 8px; font-size: 1rem; }

        .empty-state { color: #6c757d; font-style: italic; text-align: center; padding: 20px; }
        hr { margin: 25px 0; border: 0; border-top: 1px solid #eee; }
        
        @media (max-width: 992px) {
             .right-sidebar {
                max-width: 100%;
             }
        }
    </style>
</head>
<body>
<div class="container">
    <jsp:include page="common/notification.jsp" />
    <div class="main-dashboard">
        <div class="header">
            <h2>Dashboard</h2>
            <div class="user-info">
                <strong><c:out value="${hospital.name}"/></strong> | <a href="${pageContext.request.contextPath}/logout">Logout <i class="fas fa-sign-out-alt"></i></a>
            </div>
        </div>

        <div class="stat-cards">
             <div class="stat-card">
                <div class="icon"><i class="fas fa-file-medical"></i></div>
                <div class="info">
                    <h4>Pending Requests</h4>
                    <p>${fn:length(pendingRequests)}</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="icon"><i class="fas fa-truck-loading"></i></div>
                <div class="info">
                    <h4>Incoming Bags</h4>
                    <p>${fn:length(inTransitBags)}</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="icon"><i class="fas fa-calendar-check"></i></div>
                <div class="info">
                    <h4>Appointments</h4>
                    <p>${fn:length(pendingDonations)}</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="icon"><i class="fas fa-vials"></i></div>
                <div class="info">
                    <h4>Awaiting Tests</h4>
                    <p>${fn:length(pendingBags)}</p>
                </div>
            </div>
        </div>

        <div class="panel">
            <div class="panel-header"><h3><i class="fas fa-chart-bar"></i> Current Blood Stock</h3></div>
            <div class="panel-body">
                <div class="blood-stock-chart">
                    <c:forEach var="entry" items="${currentStock}">
                        <div class="blood-group-bar">
                             <div class="bar" style="height: ${entry.value * 10 > 150 ? 150 : entry.value * 10}px;">
                                <span class="unit-count">${entry.value}</span>
                             </div>
                             <div class="label">${entry.key}</div>
                        </div>
                    </c:forEach>
                     <c:if test="${empty currentStock}">
                         <p class="empty-state">No cleared stock to display.</p>
                     </c:if>
                </div>
                 <hr>
                <h4>Emergency Donor Contacts</h4>
                 <p style="font-size: 0.8em; color: #666;">
                    This list shows available emergency donors for blood types currently out of stock.
                </p>
                <div id="emergency-contacts-container"></div>
            </div>
        </div>
        
        <div class="panel">
            <div class="panel-header"><h3><i class="fas fa-tasks"></i> Processing Queue</h3></div>
            <div class="panel-body">
                 <h4>Pending Patient Blood Requests</h4>
                <c:if test="${empty pendingRequests}"><p class="empty-state">No pending patient requests.</p></c:if>
                <c:if test="${not empty pendingRequests}">
                    <table class="data-table">
                        <thead><tr><th>Patient</th><th>Group</th><th>Units</th><th>Action</th></tr></thead>
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
    
    <div class="right-sidebar">
        <div class="panel">
            <div class="panel-header"><h3><i class="fas fa-exchange-alt"></i> Stock Actions</h3></div>
            <div class="panel-body">
                <h4>Manual Stock Adjustment</h4>
                <form action="${pageContext.request.contextPath}/manual-add-stock" method="post" style="margin-bottom: 20px;">
                    <div class="form-group"><label>Blood Group:</label>
                        <select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select>
                    </div>
                    <div class="form-group"><label>Units to Add:</label><input type="number" name="units" min="1" required></div>
                    <button type="submit" class="btn btn-approve">Add to Inventory</button>
                </form>
                 <hr>
                <h4>Remove Damaged/Used Stock</h4>
                <form action="${pageContext.request.contextPath}/manual-remove-stock" method="post">
                    <div class="form-group"><label>Blood Group:</label>
                        <select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select>
                    </div>
                    <div class="form-group"><label>Units to Remove:</label><input type="number" name="units" min="1" required></div>
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
        </div>

        <div class="panel">
             <div class="panel-header"><h3><i class="fas fa-truck"></i> Incoming Queue</h3></div>
             <div class="panel-body">
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
            contactKeys.forEach(bloodGroup => {
                const groupContainer = document.createElement('div');
                const title = document.createElement('h5');
                title.style.color = '#c9302c';
                title.style.marginTop = '20px';
                title.textContent = `Needed: ${bloodGroup}`;
                groupContainer.appendChild(title);
                const table = document.createElement('table');
                table.className = 'data-table';
                table.innerHTML = `<thead><tr><th>Name</th><th>Action</th></tr></thead>`;
                
                const tbody = document.createElement('tbody');
                const donors = emergencyContacts[bloodGroup];
                donors.forEach(donor => {
                    const row = tbody.insertRow();
                    row.insertCell().textContent = donor.name;
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