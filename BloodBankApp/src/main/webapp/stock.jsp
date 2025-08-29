<%@ page import="java.sql.*, dao.DBUtil" %>
<!DOCTYPE html>
<html>
<head>
  <title>Blood Stock</title>
</head>
<body>
  <h3>Current Stock</h3>

  <table border="1" cellpadding="6">
    <tr>
      <th>Blood Group</th>
      <th>Units</th>
      <th>Last Updated</th>
    </tr>
    <%
      boolean hasData = false;
      try (Connection con = DBUtil.getConnection();
           PreparedStatement ps = con.prepareStatement(
             "SELECT blood_group, units, last_updated FROM blood_stock ORDER BY blood_group"
           );
           ResultSet rs = ps.executeQuery()) {
           
        while (rs.next()) {
          hasData = true;
    %>
          <tr>
            <td><%= rs.getString("blood_group") %></td>
            <td><%= rs.getInt("units") %></td>
            <td><%= rs.getTimestamp("last_updated") %></td>
          </tr>
    <%
        }
      } catch (Exception e) {
        out.print("<tr><td colspan='3' style='color:red;'>Error: " + e.getMessage() + "</td></tr>");
      }

      if (!hasData) {
    %>
        <tr>
          <td colspan="3" style="color:green;">No stock records found.</td>
        </tr>
    <%
      }
    %>
  </table>

  <br>
  <a href="admin.jsp">Back to Admin Dashboard</a>
</body>
</html>
