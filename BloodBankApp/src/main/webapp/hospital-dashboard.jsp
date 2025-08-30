<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Hospital, dao.StockDAO, java.util.Map" %>
<%
    // Security Check
    Hospital hospital = (Hospital) session.getAttribute("hospital");
    if (hospital == null) {
        response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
        return;
    }
    // Fetch current stock levels to display on the page
    Map<String, Integer> currentStock = StockDAO.getAllStock();
%>
<html>
<head>
    <title>Hospital Dashboard</title>
    <style>
        body { font-family: sans-serif; margin: 0; padding: 20px; background-color: #f9f9f9; }
        .container { max-width: 800px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; }
        h2, h3 { color: #c9302c; }
        .content { display: flex; gap: 40px; }
        .stock-display, .stock-management { flex: 1; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #f2f2f2; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        select, input { width: 100%; box-sizing: border-box; padding: 8px; border-radius: 4px; border: 1px solid #ccc; }
        button { background-color: #d9534f; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; width: 100%; font-size: 16px; }
        .message { padding: 10px; margin-top: 20px; border-radius: 4px; text-align: center; }
        .success { background-color: #dff0d8; color: #3c763d; }
        .error { background-color: #f2dede; color: #a94442; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Welcome, <%= hospital.getName() %></h2>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
        
        <div class="content">
            <div class="stock-display">
                <h3>Current Blood Stock</h3>
                <table>
                    <tr><th>Blood Group</th><th>Units Available</th></tr>
                    <tr><td>A+</td><td><%= currentStock.getOrDefault("A+", 0) %></td></tr>
                    <tr><td>A-</td><td><%= currentStock.getOrDefault("A-", 0) %></td></tr>
                    <tr><td>B+</td><td><%= currentStock.getOrDefault("B+", 0) %></td></tr>
                    <tr><td>B-</td><td><%= currentStock.getOrDefault("B-", 0) %></td></tr>
                    <tr><td>AB+</td><td><%= currentStock.getOrDefault("AB+", 0) %></td></tr>
                    <tr><td>AB-</td><td><%= currentStock.getOrDefault("AB-", 0) %></td></tr>
                    <tr><td>O+</td><td><%= currentStock.getOrDefault("O+", 0) %></td></tr>
                    <tr><td>O-</td><td><%= currentStock.getOrDefault("O-", 0) %></td></tr>
                </table>
            </div>

            <div class="stock-management">
                <h3>Manage Stock</h3>
                <form action="${pageContext.request.contextPath}/manage-stock" method="post">
                    <div class="form-group">
                        <label for="action">Action:</label>
                        <select id="action" name="action" required>
                            <option value="add">Add Units</option>
                            <option value="remove">Remove Units</option>
                            <option value="set">Set Total Units</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="bloodGroup">Blood Group:</label>
                        <select id="bloodGroup" name="bloodGroup" required>
                            <option value="A+">A+</option><option value="A-">A-</option>
                            <option value="B+">B+</option><option value="B-">B-</option>
                            <option value="AB+">AB+</option><option value="AB-">AB-</option>
                            <option value="O+">O+</option><option value="O-">O-</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="units">Units:</label>
                        <input type="number" id="units" name="units" min="1" required>
                    </div>
                    <button type="submit">Update Stock</button>
                </form>
            </div>
        </div>
        
        <% if (request.getParameter("success") != null) { %>
            <p class="message success"><%= request.getParameter("success").replace("+", " ") %></p>
        <% } %>
        <% if (request.getParameter("error") != null) { %>
            <p class="message error"><%= request.getParameter("error").replace("+", " ") %></p>
        <% } %>
    </div>
</body>
</html>