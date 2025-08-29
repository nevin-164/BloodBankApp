<%@ page import="model.User" %>
<%
  User u = (User) session.getAttribute("user");
  if (u == null || !"DONOR".equals(u.getRole())) { 
      response.sendRedirect("login.jsp"); 
      return; 
  }
%>
<!DOCTYPE html>
<html>
<head>
  <title>Donor Dashboard</title>
</head>
<body>
  <h3>Welcome, <%= u.getName() %> (Donor)</h3>
  <p>Next eligible date: <%= u.getNextEligibleDate() %></p>

  <form action="donate" method="post">
    <input type="hidden" name="user_id" value="<%= u.getUserId() %>">
    Blood Group: <input name="blood_group" value="<%= u.getBloodGroup() %>" required><br>
    Units: <input type="number" name="units" min="1" value="1"><br>
    <button type="submit">Record Donation</button>
  </form>

  <p style="color:green;">
    <%= request.getAttribute("msg") == null ? "" : request.getAttribute("msg") %>
  </p>

  <a href="index.jsp">Home</a>
</body>
</html>
