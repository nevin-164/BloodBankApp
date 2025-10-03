<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, dao.*, model.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Security check for Admin role
    User u = (User) session.getAttribute("user");
    if (u == null || !"ADMIN".equals(u.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<Hospital> hospitals = HospitalDAO.getAllHospitals();
    request.setAttribute("hospitals", hospitals);

    Map<String, Integer> selectedStock = null;
    String selectedHospitalId = request.getParameter("hospital_id");

    if (selectedHospitalId != null && !selectedHospitalId.isEmpty()) {
        selectedStock = StockDAO.getStockByHospital(Integer.parseInt(selectedHospitalId));
        request.setAttribute("selectedStock", selectedStock);
        request.setAttribute("selectedHospitalId", selectedHospitalId);
    }
%>
<html>
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>PLASMIC - Admin Stock Management</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* ✅ NEW: Universal box-sizing and a modern font stack */
        * {
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            background-color: #f8f9fa;
        }
        
        .container {
            padding: 30px;
        }
        
        h2 {
            text-align: center;
            color: #c9302c;
        }
        
        /* ✅ UPDATED: Layout is now more flexible */
        .dashboard-layout {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 40px;
            max-width: 1200px;
            margin: 20px auto;
        }
        
        .panel {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        h3 {
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
            margin-top: 0;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            background-color: #dc3545;
            color: white;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        select, input {
            width: 100%;
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #ccc;
            font-size: 16px; /* Prevents iOS auto-zoom */
        }
        
        button {
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
        }
        
        .message {
            text-align: center;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 5px;
            font-weight: bold;
        }
        
        .success { color: #155724; background-color: #d4edda; }
        .error { color: #721c24; background-color: #f8d7da; }

        /* --- ✅ NEW: Media Query for Mobile Devices --- */
        @media (max-width: 768px) {
            .container {
                padding: 15px;
            }
            .dashboard-layout {
                grid-template-columns: 1fr; /* Stack the grid into a single column */
                gap: 20px;
            }
            
            /* Responsive Table Styling */
            table, thead, tbody, th, td, tr {
                display: block;
            }
            thead tr {
                position: absolute; /* Hide original headers */
                top: -9999px;
                left: -9999px;
            }
            tr {
                border: 1px solid #ccc;
                margin-bottom: 10px;
                border-radius: 5px;
            }
            td {
                border: none;
                border-bottom: 1px solid #eee;
                position: relative;
                padding-left: 50%;
                text-align: right;
                min-height: 40px; /* Ensure consistent height */
                display: flex;
                align-items: center;
                justify-content: flex-end;
            }
            td:last-child {
                border-bottom: 0;
            }
            td:before {
                content: attr(data-label); /* Use data-label for the new "header" */
                position: absolute;
                left: 12px;
                width: 45%;
                padding-right: 10px;
                white-space: nowrap;
                text-align: left;
                font-weight: bold;
            }
        }
    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />
    <div class="container">
        <h2>Admin Stock Management</h2>

        <div class="panel" style="max-width: 600px; margin: 0 auto 20px auto;">
            <form method="GET" action="stock.jsp">
                <div class="form-group">
                    <label for="hospital_id">Select a Hospital to Manage Stock:</label>
                    <select name="hospital_id" id="hospital_id" onchange="this.form.submit()">
                        <option value="">-- Choose a Hospital --</option>
                        <c:forEach var="h" items="${hospitals}">
                            <option value="${h.id}" ${h.id == selectedHospitalId ? 'selected' : ''}>${h.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </form>
        </div>

        <c:if test="${not empty selectedHospitalId}">
            <div class="dashboard-layout">
                <div class="panel">
                    <h3>Current Stock Levels</h3>
                    <table>
                        <thead><tr><th>Blood Group</th><th>Units</th></tr></thead>
                        <tbody>
                            <c:forEach var="entry" items="${selectedStock}">
                                <tr>
                                    <td data-label="Blood Group">${entry.key}</td>
                                    <td data-label="Units">${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="panel">
                    <h3>Update Stock</h3>
                    
                    
                    <form method="POST" action="${pageContext.request.contextPath}/admin/update-stock">
                        <input type="hidden" name="hospital_id" value="${selectedHospitalId}">
                        <div class="form-group">
                            <label for="blood_group">Blood Group:</label>
                            <select name="blood_group" id="blood_group" required>
                                <option value="A+">A+</option><option value="A-">A-</option>
                                <option value="B+">B+</option><option value="B-">B-</option>
                                <option value="AB+">AB+</option><option value="AB-">AB-</option>
                                <option value="O+">O+</option><option value="O-">O-</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="units">Set Total Units To:</label>
                            <input type="number" name="units" id="units" min="0" required>
                        </div>
                        <button type="submit">Update Stock</button>
                    </form>
                </div>
            </div>
        </c:if>
        
        <a href="admin.jsp" style="display:block; text-align:center; margin-top:20px;">← Back to Dashboard</a>
    </div>
</body>
</html>