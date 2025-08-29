<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
  <title>Expiry Alerts</title>
</head>
<body>
  <h3>Expiring Soon (within 7 days)</h3>

  <%
  @SuppressWarnings("unchecked")
    List<String[]> exp = (List<String[]>) request.getAttribute("expiring");
    if (exp != null && !exp.isEmpty()) {
  %>
    <table border="1" cellpadding="6">
      <tr>
        <th>Blood Group</th>
        <th>Donation Date</th>
        <th>Expiry Date</th>
        <th>Units</th>
      </tr>
      <%
        for (String[] r : exp) {
      %>
      <tr>
        <td><%= r[0] %></td>
        <td><%= r[1] %></td>
        <td><%= r[2] %></td>
        <td><%= r[3] %></td>
      </tr>
      <% } %>
    </table>
  <%
    } else {
  %>
    <p style="color:green;">No units expiring within the next 7 days 🎉</p>
  <%
    }
  %>

  <br>
  <a href="admin.jsp">Back to Admin Dashboard</a>
</body>
</html>
