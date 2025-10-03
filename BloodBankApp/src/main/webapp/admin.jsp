<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>PLASMIC - Admin Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        * {
            box-sizing: border-box;
        }
        body {
            font-family: 'Poppins', sans-serif;
            margin: 0;
            background-color: #f8f9fa;
        }
        .header {
            background-color: #343a40;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }
        .header h2 {
            margin: 0;
            font-size: 22px;
        }
        .header a {
            color: #d3d3d3;
            text-decoration: none;
            font-size: 16px;
        }
        .header a:hover {
            color: white;
        }
        .container {
            padding: 30px;
        }
        .welcome-banner {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }
        .dashboard-card {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 20px;
            text-align: center;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .dashboard-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
        }
        .dashboard-card a {
            text-decoration: none;
            color: #333;
            font-size: 18px;
            font-weight: bold;
        }
        .card-icon {
            width: 96px;
            height: 96px;
            margin-bottom: 15px;
        }
        @media (max-width: 600px) {
            .header {
                padding: 15px;
                flex-direction: column;
                align-items: flex-start;
            }
            .container {
                padding: 15px;
            }
            .welcome-banner, .dashboard-card {
                padding: 15px;
            }
        }
    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />
    <div class="header">
        <h2>PLASMIC Admin Dashboard</h2>
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>

    <div class="container">
        <div class="welcome-banner">
            <h3>Welcome back, <%= u.getName() %>!</h3>
            <p>From this dashboard, you can manage all aspects of the blood bank application.</p>
        </div>

        <div class="dashboard-grid">
            
            <%-- ✅ NEW: Link to Donor CRM --%>
            <div class="dashboard-card">
                <img src="<%= request.getContextPath() %>/images/icon-users.png" alt="Donor CRM" class="card-icon">
                <br>
                <a href="${pageContext.request.contextPath}/admin/donor-list">Donor Management (CRM)</a>
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
             <div class="dashboard-card">
                <img src="<%= request.getContextPath() %>/images/icon-users.png" alt="Manage Users" class="card-icon">
                <br>
                <a href="<%= request.getContextPath() %>/admin/users">Manage Users</a>
            </div>
            
        </div>
    </div>
</body>
</html>