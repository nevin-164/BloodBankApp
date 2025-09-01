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
    <title>PLASMIC - Admin Stock Management</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { padding: 30px; }
        h2 { text-align: center; color: #c9302c; }
        .dashboard-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 40px; width: 80%; margin: 20px auto; }
        .panel { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h3 { border-bottom: 2px solid #eee; padding-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #dc3545; color: white; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        select, input { width: 100%; padding: 8px; border-radius: 5px; border: 1px solid #ccc; box-sizing: border-box; }
        button { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }
        .message { text-align: center; padding: 10px; border-radius: 5px; font-weight: bold; }
        .success { color: #155724; background-color: #d4edda; }
        .error { color: #721c24; background-color: #f8d7da; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Admin Stock Management</h2>

        <div class="panel">
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
                                <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="panel">
                    <h3>Update Stock</h3>
                    <c:if test="${not empty param.success}"><p class="message success">${param.success}</p></c:if>
                    <c:if test="${not empty param.error}"><p class="message error">${param.error}</p></c:if>
                    
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