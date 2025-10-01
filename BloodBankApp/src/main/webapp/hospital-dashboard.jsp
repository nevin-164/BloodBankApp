<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    // Security check and data retrieval
    Hospital hospital = (Hospital) session.getAttribute("hospital");
    if (hospital == null) {
        response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
        return;
    }
    String successMessage = request.getParameter("success");
    String errorMessage = request.getParameter("error");
%>
<html>
<head>
    <title>PLASMIC - Hospital Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }
        body { font-family: 'Poppins', sans-serif; margin: 0; background: #f4f7f9; padding: 20px; }
        .container { max-width: 1400px; margin: 0 auto; background: #ffffff; padding: 20px 40px; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.08); }
        .header { display: flex; justify-content: space-between; align-items: center; background: linear-gradient(90deg, #d9534f 0%, #c9302c 100%); color: white; padding: 20px 30px; margin: -20px -40px 30px -40px; border-radius: 15px 15px 0 0; flex-wrap: wrap; gap: 15px; }
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
        .panel { padding: 25px; border-radius: 10px; background-color: #fff; height: 100%; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #555;}
        .form-group select, .form-group input { width: 100%; padding: 12px; border: 1px solid #ccc; border-radius: 8px; font-size: 1rem; transition: border-color 0.2s; }
        .form-group select:focus, .form-group input:focus { border-color: #d9534f; outline: none; }
        .form-group button { width: 100%; padding: 12px; }
        .empty-state { color: #6c757d; font-style: italic; margin-top: 15px; text-align: center; padding: 20px; }
        hr { margin:25px 0; border: 0; border-top: 1px solid #eee; }

        .modal { display: none; position: fixed; z-index: 1001; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.5); }
        .modal-content { background-color: #fefefe; margin: 15% auto; padding: 20px; border-radius: 10px; width: 80%; max-width: 500px; }
        .modal-header { padding: 10px 0; border-bottom: 1px solid #eee; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }
        .modal-header h4 { margin: 0; color: #d9534f; }
        .close-btn { color: #aaa; font-size: 28px; font-weight: bold; cursor: pointer; }
        .donor-list label { display: block; padding: 10px; border-radius: 5px; margin-bottom: 5px; cursor: pointer; transition: background-color 0.2s; }
        .donor-list label:hover { background-color: #f5f5f5; }
        .donor-list input { margin-right: 15px; transform: scale(1.2); }
        .modal-footer { padding-top: 20px; text-align: right; border-top: 1px solid #eee; margin-top: 20px; }
        .btn-confirm { border-radius: 8px; width: auto; }
        
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
                            <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
                        </c:forEach>
                        <c:if test="${empty currentStock}"><td colspan="2" class="empty-state">No cleared stock.</td></c:if>
                    </tbody>
                </table>
                
                <hr>
                <h4>Emergency Donor Contacts</h4>
                <p style="font-size: 0.8em; color: #666;">Available donors for out-of-stock blood types.</p>
                <c:if test="${empty emergencyContacts}"><p class="empty-state">No emergency contacts needed.</p></c:if>
                <c:if test="${not empty emergencyContacts}">
                    <c:forEach var="entry" items="${emergencyContacts}">
                        <h5 style="color: #c9302c; margin-top: 20px;">Needed: ${entry.key}</h5>
                        <table class="data-table">
                            <thead><tr><th>Name</th><th>Contact</th><th>Action</th></tr></thead>
                            <tbody>
                                <c:forEach var="donor" items="${entry.value}">
                                    <tr><td>${donor.name}</td><td>${donor.email}</td><td><a href="tel:${donor.contactNumber}" class="btn btn-call">Call</a></td></tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:forEach>
                </c:if>

                <hr>
                <h4>Manual Stock Adjustment</h4>
                <form action="${pageContext.request.contextPath}/manual-add-stock" method="post">
                    <div class="form-group"><label>Blood Group:</label><select name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select></div>
                    <div class="form-group"><label>Units to Add:</label><input type="number" name="units" min="1" required></div>
                    <button type="submit" class="btn btn-approve">Add Stock</button>
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
                                    <td class="actions"><a href="${pageContext.request.contextPath}/receive-transfer?bagId=${bag.bagId}" class="btn btn-approve" onclick="return confirm('Mark bag as received?');">Receive</a></td>
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
                                        <a href="${pageContext.request.contextPath}/approve-transfer?transferId=${transfer.transferId}&status=APPROVED" class="btn btn-approve">Approve</a>
                                        <a href="${pageContext.request.contextPath}/approve-transfer?transferId=${transfer.transferId}&status=DECLINED" class="btn btn-decline">Decline</a>
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
                                       <button class="btn btn-warning" onclick="openDonorModal('${req.requestId}', '${req.bloodGroup}')">
                                           Via Donor
                                       </button>
                                       <form action="${pageContext.request.contextPath}/decline-request" method="post" style="display:inline;">
                                           <input type="hidden" name="requestId" value="${req.requestId}">
                                           <button type="submit" class="btn btn-decline">Decline</button>
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
                                           <form action="${pageContext.request.contextPath}/update-donation-status" method="POST" style="display: inline;">
                                               <input type="hidden" name="donationId" value="${appt.donationId}">
                                               <input type="hidden" name="newStatus" value="PRE-SCREEN_PASSED">
                                               <button type="submit" class="btn btn-prescreen">Pass Screen</button>
                                           </form>
                                       </c:if>
                                       <c:if test="${appt.status == 'PRE-SCREEN_PASSED'}">
                                            <!-- ====== UPDATED: require manual date selection before approving ====== -->
                                            <form action="${pageContext.request.contextPath}/approve-donation" method="POST" style="display:inline;">
                                                <input type="hidden" name="donationId" value="${appt.donationId}">
                                                
                                                <!-- Manual donation date picker (required) -->
                                                <input type="date" id="donationDate-${appt.donationId}" name="donationDate" required
                                                       style="padding:6px; border-radius:6px; border:1px solid #ccc; margin-right:6px;">

                                                <button type="submit" class="btn btn-approve">Complete</button>
                                            </form>
                                       </c:if>
                                       <form action="${pageContext.request.contextPath}/update-donation-status" method="POST" style="display: inline;">
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
                                   <td>${bag.bagId}</td><td>${bag.bloodGroup}</td>
                                   <td><fmt:formatDate value="${bag.dateDonated}" pattern="yyyy-MM-dd" /></td>
                                   <td class="actions"><a href="${pageContext.request.contextPath}/update-inventory-status?bagId=${bag.bagId}&status=CLEARED" class="btn btn-approve">Clear</a></td>
                               </tr>
                           </c:forEach>
                       </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </div>

    <div id="donorModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h4>Select Emergency Donor(s)</h4>
                <span class="close-btn" onclick="closeDonorModal()">&times;</span>
            </div>
            <form id="donorForm" action="${pageContext.request.contextPath}/fulfill-via-emergency" method="post">
                <div class="modal-body">
                    <p>Select the donor(s) you have contacted to fulfill this request.</p>
                    <input type="hidden" id="modalRequestId" name="requestId">
                    <div id="modalDonorList" class="donor-list"></div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-approve btn-confirm">Confirm Fulfillment</button>
                </div>
            </form>
        </div>
    </div>
    
    <div id="toast-container"></div>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            function showToast(message, type) {
                if (!message || message.trim() === 'null') return;
                const container = document.getElementById('toast-container');
                const toast = document.createElement('div');
                toast.className = `toast ${type}`;
                // decode server-encoded query param text if needed
                try {
                    toast.textContent = decodeURIComponent(message.replace(/\+/g, ' '));
                } catch (e) {
                    toast.textContent = message;
                }
                container.appendChild(toast);
                setTimeout(() => toast.remove(), 5000);
            }
            showToast("<%= successMessage %>", 'success');
            showToast("<%= errorMessage %>", 'error');
        });

        const emergencyContacts = {
            <c:forEach var="entry" items="${emergencyContacts}" varStatus="status">
                '${entry.key}': [
                    <c:forEach var="donor" items="${entry.value}" varStatus="donorStatus">
                        { id: ${donor.id}, name: '${donor.name}' }
                        ${!donorStatus.last ? ',' : ''}
                    </c:forEach>
                ]
                ${!status.last ? ',' : ''}
            </c:forEach>
        };

        const modal = document.getElementById('donorModal');
        const modalRequestIdInput = document.getElementById('modalRequestId');
        const modalDonorListDiv = document.getElementById('modalDonorList');

        function openDonorModal(requestId, bloodGroup) {
            modalRequestIdInput.value = requestId;
            modalDonorListDiv.innerHTML = '';
            const donors = emergencyContacts[bloodGroup];

            if (donors && donors.length > 0) {
                donors.forEach(donor => {
                    const label = document.createElement('label');
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.name = 'donorId';
                    checkbox.value = donor.id;
                    label.appendChild(checkbox);
                    label.appendChild(document.createTextNode(` ${donor.name}`));
                    modalDonorListDiv.appendChild(label);
                });
            } else {
                modalDonorListDiv.innerHTML = `<p class="empty-state">No available emergency donors for ${bloodGroup}.</p>`;
            }
            modal.style.display = 'block';
        }

        function closeDonorModal() {
            modal.style.display = 'none';
        }

        window.onclick = function(event) {
            if (event.target == modal) {
                closeDonorModal();
            }
        }
    </script>
</body>
</html>
