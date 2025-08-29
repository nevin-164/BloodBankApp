<%@ page import="model.User" %>
<%
  User u = (User) session.getAttribute("user");
  if (u == null || !"PATIENT".equals(u.getRole())) { 
      response.sendRedirect("login.jsp"); 
      return; 
  }
%>
<!DOCTYPE html>
<html>
<head>
  <title>Patient Dashboard</title>
</head>
<body>
  <h3>Welcome, <%= u.getName() %> (Patient)</h3>

  <form action="request-blood" method="post">
    <input type="hidden" name="patient_id" value="<%= u.getUserId() %>">
    Blood Group Needed: <input name="blood_group" required><br>
    Units: <input type="number" name="units" min="1" value="1"><br>
    <button type="submit">Request Blood</button>
  </form>

  <p style="color:green;">
    <%= request.getAttribute("msg") == null ? "" : request.getAttribute("msg") %>
  </p>

  <a href="index.jsp">Home</a>
</body>
</html>
