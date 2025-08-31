<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Hospital, dao.StockDAO, dao.RequestDAO, model.Request, java.util.Map, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Security Check: Ensure a hospital is logged in
    Hospital hospital = (Hospital) session.getAttribute("hospital");
    if (hospital == null) {
        response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
        return;
    }
    // Fetch all necessary data for the dashboard display
    Map<String, Integer> currentStock = StockDAO.getAllStock();
    List<Request> pendingRequests = RequestDAO.getPendingRequests();
    request.setAttribute("pendingRequests", pendingRequests);
%>
<html>
<head>
    <title>Hospital Dashboard</title>
    <style>
        body { font-family: sans-serif; margin: 0; padding: 20px; background-color: #f9f9f9; }
        .container { max-width: 900px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; }
        h2, h3 { color: #c9302c; }
        .content-grid { display: grid; grid-template-columns: 1fr 2fr; gap: 40px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        .actions-cell { display: flex; gap: 10px; align-items: center; }
        .message { padding: 10px; margin: 20px 0; border-radius: 4px; text-align: center; font-weight: bold; }
        .success { background-color: #dff0d8; color: #3c763d; }
        .error { background-color: #f2dede; color: #a94442; }
        a.btn, button.btn { border: none; color: white; padding: 5px 10px; border-radius: 4px; text-decoration: none; cursor: pointer; font-family: sans-serif; font-size: 14px; }
        .approve-btn { background-color: #28a745; }
        .decline-btn { background-color: #dc3545; }
        .call-btn { background-color: #007bff; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= hospital.getName() %></h2>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        
        <%-- Display success or error messages from redirects --%>
        <c:if test="${not empty param.success}"><p class="message success">${param.success.replace('+', ' ')}</p></c:if>
        <c:if test="${not empty param.error}"><p class="message error">${param.error.replace('+', ' ')}</p></c:if>

        <div class="content-grid">
            <div class="stock-display">
                <h3>Current Stock</h3>
                <table>
                    <thead><tr><th>Blood Group</th><th>Units</th></tr></thead>
                    <tbody>
                        <c:forEach var="entry" items="<%= currentStock.entrySet() %>">
                            <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="requests-management">
                <h3>Pending Blood Requests</h3>
                <c:if test="${empty pendingRequests}">
                    <p>There are no pending blood requests at this time.</p>
                </c:if>
                <c:if test="${not empty pendingRequests}">
                    <table>
                       <thead><tr><th>Patient</th><th>Contact</th><th>Blood Group</th><th>Units</th><th>Actions</th></tr></thead>
                       <tbody>
                           <c:forEach var="req" items="${pendingRequests}">
                               <tr>
                                   <td>${req.patientName}</td>
                                   <td>
                                       <button class="btn call-btn" onclick="callPatient('${req.patientName}', '${req.patientPhone}')">Call</button>
                                   </td>
                                   <td>${req.bloodGroup}</td>
                                   <td>${req.units}</td>
                                   <td class="actions-cell">
                                       <a href="${pageContext.request.contextPath}/approve-request?requestId=${req.requestId}" class="btn approve-btn">Approve</a>
                                       <a href="${pageContext.request.contextPath}/decline-request?requestId=${req.requestId}" class="btn decline-btn" onclick="return confirm('Decline this request?');">Decline</a>
                                   </td>
                               </tr>
                           </c:forEach>
                       </tbody>
                    </table>
                </c:if>
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