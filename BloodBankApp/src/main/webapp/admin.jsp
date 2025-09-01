<%@ page import="model.User" %>
<%
    User u = (User) session.getAttribute("user");
    if (u == null || !"ADMIN".equals(u.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>PLASMIC - Admin Dashboard</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .header { background-color: #343a40; color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
        .header h2 { margin: 0; }
        .header a { color: #d3d3d3; text-decoration: none; }
        .header a:hover { color: white; }
        .container { padding: 30px; }
        .welcome-banner { background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); margin-bottom: 30px; }
        .dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
        .dashboard-card { background-color: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 20px; text-align: center; transition: transform 0.2s; }
        .dashboard-card:hover { transform: translateY(-5px); }
        .dashboard-card a { text-decoration: none; color: #333; font-size: 18px; font-weight: bold; }
        
        /* ✅ MODIFIED: Increased the width and height for bigger, identical icons */
        .card-icon {
            width: 96px;
            height: 96px;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h2>PLASMIC Admin Dashboard</h2>
        <a href="<%=request.getContextPath()%>/logout">Logout</a>
    </div>

    <div class="container">
        <div class="welcome-banner">
            <h3>Welcome back, <%= u.getName() %>!</h3>
            <p>From this dashboard, you can manage all aspects of the blood bank application.</p>
        </div>

        <div class="dashboard-grid">
            <div class="dashboard-card">
                <img src="<%= request.getContextPath() %>/images/icon-users.png" alt="Manage Users" class="card-icon">
                <br>
                <a href="<%= request.getContextPath() %>/admin/users">Manage Users</a>
            </div>
            <div class="dashboard-card">
                <img src="<%= request.getContextPath() %>/images/icon-hospitals.png" alt="Manage Hospitals" class="card-icon">
                <br>
                <a href="${pageContext.request.contextPath}/admin/hospitals">Manage Hospitals</a>
            </div>
            <div class="dashboard-card">
                <img src="<%= request.getContextPath() %>/images/icon-stock.png" alt="View Stock" class="card-icon">
                <br>
                <a href="stock.jsp">View Stock Levels</a>
            </div>
            <div class="dashboard-card">
                <img src="<%= request.getContextPath() %>/images/icon-alerts.png" alt="View Alerts" class="card-icon">
                <br>
                <a href="alerts.jsp">View Expiry Alerts</a>
            </div>
        </div>
    </div>
</body>
</html>