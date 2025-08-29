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

  <h3>Admin Dashboard</h3>
  <ul>
    <li><a href="alerts">View Expiry Alerts (7 days)</a></li>
    <li><a href="stock.jsp">View Stock</a></li>
  </ul>

  <a href="index.jsp">Home</a>
</body>
</html>
