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
    <title>Admin Dashboard</title>
</head>
<body>
    <h3>Welcome, <%= u.getName() %> (Admin)</h3>

    <ul>
        <!-- updated to go through servlet -->
        <li><a href="<%= request.getContextPath() %>/admin/users">View Registered Users</a></li>
        <li><a href="alerts.jsp">View Expiry Alerts</a></li>
        <li><a href="stock.jsp">View Stock</a></li>
    </ul>
 <li>
    <a href="${pageContext.request.contextPath}/hospitalList">Manage Hospitals</a>
</li>
 
    

    <a href="<%=request.getContextPath()%>/logout">Logout</a>
    
</body>
</html>
