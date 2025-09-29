<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*, model.BloodInventory, model.StockTransfer, model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Security check remains the same. The messages will now be handled by JavaScript.
    Hospital hospital = (Hospital) session.getAttribute("hospital");
    if (hospital == null) {
        response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
        return;
    }
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    if (successMessage != null) session.removeAttribute("successMessage");
    if (errorMessage != null) session.removeAttribute("errorMessage");
%>
<html>
<head>
    <title>PLASMIC - Hospital Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }
        body { 
            font-family: 'Poppins', sans-serif; 
            margin: 0; 
            background: linear-gradient(to right top, #f4f6f9, #ffffff);
            padding: 20px;
        }
        .container { 
            max-width: 1400px; 
            margin: 0 auto; 
            background: #ffffff; 
            padding: 20px 40px; 
            border-radius: 15px; 
            box-shadow: 0 10px 30px rgba(0,0,0,0.1); 
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
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { padding: 12px 10px; text-align: left; border-bottom: 1px solid #eee; font-size: 0.9rem;}
        th { background-color: #f8f9fa; font-weight: 600; color: #333; }
        tbody tr:hover { background-color: #fdf5f5; transition: background-color 0.2s; }
        .actions-cell { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
        a.btn, button.btn { border: none; color: white; padding: 8px 15px; border-radius: 20px; text-decoration: none; cursor: pointer; font-family: 'Poppins', sans-serif; font-size: 14px; font-weight: 600; white-space: nowrap; transition: all 0.3s ease; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        a.btn:hover, button.btn:hover { transform: translateY(-2px); box-shadow: 0 4px 10px rgba(0,0,0,0.15); }
        .approve-btn { background: linear-gradient(45deg, #28a745, #218838); }
        .decline-btn { background: linear-gradient(45deg, #dc3545, #c82333); }
        .call-btn { background: linear-gradient(45deg, #007bff, #0056b3); }
        .panel { padding: 25px; border-radius: 10px; background-color: #fff; height: 100%; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #555;}
        .form-group select, .form-group input { width: 100%; padding: 12px; border: 1px solid #ccc; border-radius: 8px; font-size: 1rem; transition: border-color 0.2s; }
        .form-group select:focus, .form-group input:focus { border-color: #d9534f; outline: none; }
        .form-group button { width: 100%; padding: 12px; }
        .empty-state { color: #6c757d; font-style: italic; margin-top: 15px; text-align: center; padding: 20px; }
        .receive-all-btn { background: linear-gradient(45deg, #17a2b8, #138496); width: 100%; margin-top: 15px; }
        hr { margin:25px 0; border: 0; border-top: 1px solid #eee; }

        #toast-container { position: fixed; top: 20px; right: 20px; z-index: 1000; }
        .toast { padding: 15px 25px; margin-bottom: 10px; border-radius: 8px; color: white; font-weight: 600; box-shadow: 0 5px 15px rgba(0,0,0,0.2); animation: slideIn 0.5s, fadeOut 0.5s 4.5s; }
        .toast.success { background-color: #28a745; }
        .toast.error { background-color: #dc3545; }
        @keyframes slideIn { from { transform: translateX(100%); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
        @keyframes fadeOut { from { opacity: 1; } to { opacity: 0; } }

        @media (max-width: 1200px) { .dashboard-layout { grid-template-columns: 1fr 1fr; } }
        @media (max-width: 768px) { .dashboard-layout { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>PLASMIC Hospital Portal</h2>
            <a href="${pageContext.request.contextPath}/logout">Logout, <%= hospital.getName() %></a>
        </div>
        
        <div class="dashboard-layout">
            <!-- COLUMN 1: STOCK MANAGEMENT -->
            <div class="panel">
                <h3 class="stock-header">Stock & Transfers</h3>
                <h4>Current Cleared Stock</h4>
                <table>
                    <thead><tr><th>Blood Group</th><th>Units</th></tr></thead>
                    <tbody>
                        <c:forEach var="entry" items="${currentStock}">
                            <tr>
                                <td>${entry.key}</td>
                                <td>${entry.value}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty currentStock}">
                            <tr>
                                <td colspan="2" class="empty-state">No cleared stock.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
                <hr>
                <h4>Manual Stock Adjustment</h4>
                <form action="${pageContext.request.contextPath}/manual-add-stock" method="post">
                    <div class="form-group"><label>Blood Group:</label><select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select></div>
                    <div class="form-group"><label>Units to Add:</label><input type="number" name="units" min="1" required></div>
                    <button type="submit" class="btn approve-btn">Add to Inventory</button>
                </form>
                <form action="${pageContext.request.contextPath}/manual-remove-stock" method="post" style="margin-top: 15px;">
                     <div class="form-group"><label>Blood Group:</label><select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select></div>
                    <div class="form-group"><label>Units to Remove:</label><input type="number" name="units" min="1" required></div>
                    <button type="submit" class="btn decline-btn" onclick="return confirm('Are you sure you want to permanently remove these units? This action cannot be undone.');">Remove from Inventory</button>
                </form>
                <hr>
                <h4>Request Stock Transfer</h4>
                <form action="${pageContext.request.contextPath}/request-transfer" method="post">
                    <div class="form-group"><label>Request From:</label><select name="supplyingHospitalId" required><c:forEach var="h" items="${otherHospitals}"><option value="${h.id}">${h.name}</option></c:forEach></select></div>
                    <div class="form-group"><label>Blood Group:</label><select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select></div>
                    <div class="form-group"><label>Units:</label><input type="number" name="units" min="1" required></div>
                    <button type="submit" class="btn call-btn">Submit Request</button>
                </form>
            </div>
            <!-- COLUMN 2: INCOMING ITEMS -->
            <div class="panel">
                <h3 class="incoming-header">Incoming Queue</h3>
                <h4>Incoming Shipments (In-Transit)</h4>
                <c:if test="${empty inTransitBags}"><p class="empty-state">No shipments in transit.</p></c:if>
                <c:if test="${not empty inTransitBags}">
                    <a href="${pageContext.request.contextPath}/receive-all" class="btn receive-all-btn" onclick="return confirm('Receive all pending shipments?');">Receive All Shipments</a>
                    <table>
                        <thead><tr><th>Bag ID</th><th>Blood Group</th><th>Action</th></tr></thead>
                        <tbody>
                            <c:forEach var="bag" items="${inTransitBags}">
                                <tr>
                                    <td>${bag.bagId}</td><td>${bag.bloodGroup}</td>
                                    <td class="actions-cell"><a href="${pageContext.request.contextPath}/receive-transfer?bagId=${bag.bagId}" class="btn approve-btn" onclick="return confirm('Mark bag as received?');">Receive</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <hr>
                <h4>Incoming Stock Transfer Requests</h4>
                <c:if test="${empty pendingTransfers}"><p class="empty-state">No incoming requests.</p></c:if>
                <c:if test="${not empty pendingTransfers}">
                    <table>
                        <thead><tr><th>From</th><th>Group</th><th>Units</th><th>Action</th></tr></thead>
                        <tbody>
                            <c:forEach var="transfer" items="${pendingTransfers}">
                                <tr>
                                    <td>${transfer.requestingHospitalName}</td><td>${transfer.bloodGroup}</td><td>${transfer.units}</td>
                                    <td class="actions-cell">
                                        <a href="${pageContext.request.contextPath}/approve-transfer?transferId=${transfer.transferId}&status=APPROVED" class="btn approve-btn" onclick="return confirm('Approve this transfer?');">Approve</a>
                                        <a href="${pageContext.request.contextPath}/approve-transfer?transferId=${transfer.transferId}&status=DECLINED" class="btn decline-btn" onclick="return confirm('Decline this transfer?');">Decline</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
            <!-- COLUMN 3: DONATION AND INVENTORY PROCESSING -->
            <div class="panel">
                <h3 class="processing-header">Processing Queue</h3>
                
                <!-- ✅ FIXED: Re-added the Pending Patient Blood Requests panel -->
                <h4>Pending Patient Blood Requests</h4>
                <c:if test="${empty pendingRequests}"><p class="empty-state">No pending patient requests.</p></c:if>
                <c:if test="${not empty pendingRequests}">
                    <table>
                       <thead><tr><th>Patient</th><th>Group</th><th>Units</th><th>Action</th></tr></thead>
                       <tbody>
                           <c:forEach var="req" items="${pendingRequests}">
                               <tr>
                                   <td>${req.patientName}</td>
                                   <td>${req.bloodGroup}</td>
                                   <td>${req.units}</td>
                                   <td class="actions-cell">
                                       <a href="${pageContext.request.contextPath}/approve-request?requestId=${req.id}" class="btn approve-btn">Approve</a>
                                       <a href="${pageContext.request.contextPath}/decline-request?requestId=${req.id}" class="btn decline-btn">Decline</a>
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
                    <table>
                       <thead><tr><th>Donor</th><th>Status</th><th>Action</th></tr></thead>
                       <tbody>
                           <c:forEach var="appt" items="${pendingDonations}">
                               <tr>
                                   <td>${appt.donorName}</td><td>${appt.status}</td>
                                   <td class="actions-cell">
                                       <c:if test="${appt.status == 'PENDING'}"><a href="${pageContext.request.contextPath}/update-donation-status?donationId=${appt.donationId}&status=PRE-SCREEN_PASSED" class="btn approve-btn">Pass Screen</a></c:if>
                                       <c:if test="${appt.status == 'PRE-SCREEN_PASSED'}"><a href="${pageContext.request.contextPath}/approve-donation?donationId=${appt.donationId}" class="btn approve-btn">Confirm Donation</a></c:if>
                                       <a href="${pageContext.request.contextPath}/update-donation-status?donationId=${appt.donationId}&status=CANCELLED" class="btn decline-btn">Cancel</a>
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
                    <table>
                       <thead><tr><th>Bag ID</th><th>Group</th><th>Donated On</th><th>Action</th></tr></thead>
                       <tbody>
                           <c:forEach var="bag" items="${pendingBags}">
                               <tr>
                                   <td>${bag.bagId}</td><td>${bag.bloodGroup}</td><td>${bag.dateDonated}</td>
                                   <td class="actions-cell"><a href="${pageContext.request.contextPath}/update-inventory-status?bagId=${bag.bagId}&status=CLEARED" class="btn approve-btn" onclick="return confirm('Clear this bag for use?');">Clear</a></td>
                               </tr>
                           </c:forEach>
                       </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </div>

    <div id="toast-container"></div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            function showToast(message, type) {
                const container = document.getElementById('toast-container');
                const toast = document.createElement('div');
                toast.className = `toast ${type}`;
                toast.textContent = message;
                container.appendChild(toast);
                setTimeout(() => {
                    toast.style.animation = 'fadeOut 0.5s forwards';
                    setTimeout(() => {
                        toast.remove();
                    }, 500);
                }, 4500);
            }
            const successMessage = "<%= successMessage != null ? successMessage : "" %>";
            const errorMessage = "<%= errorMessage != null ? errorMessage : "" %>";
            if (successMessage) {
                showToast(successMessage, 'success');
            }
            if (errorMessage) {
                showToast(errorMessage, 'error');
            }
        });
    </script>
</body>
</html>

