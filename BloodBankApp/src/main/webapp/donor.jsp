<%@ page import="model.User, dao.HospitalDAO, model.Hospital, java.util.List" %>
<%
  User u = (User) session.getAttribute("user");
  if (u == null || !"DONOR".equals(u.getRole())) { 
      response.sendRedirect("login.jsp"); 
      return;
  }
  // This is needed for the new donation form with the hospital dropdown
  List<Hospital> hospitals = (List<Hospital>) request.getAttribute("hospitals");
%>
<!DOCTYPE html>
<html>
<head>
  <title>Donor Dashboard</title>
</head>
<body>
  <h3>Welcome, <%= u.getName() %> (Donor)</h3>
  <p>Your Blood Group: <%= u.getBloodGroup() %></p>
  <p>Next eligible date for donation: <%= u.getNextEligibleDate() %></p>

  <hr>
  <h3>Record a New Donation</h3>
  <form action="donate" method="post">
    
    <label for="hospital_id">Select Hospital:</label>
    <select name="hospital_id" id="hospital_id" required>
        <% if (hospitals != null) { 
            for (Hospital h : hospitals) { %>
                <option value="<%= h.getId() %>"><%= h.getName() %></option>
            <% }
        } %>
    </select>
    <br><br>

    <label for="units">Units Donated:</label>
    <input type="number" id="units" name="units" min="1" value="1" required>
    <br><br>
    
    <button type="submit">Record Donation</button>
  </form>

  <p style="color:green; font-weight:bold;">
    <%= request.getAttribute("msg") == null ? "" : request.getAttribute("msg") %>
  </p>

  <br>
  <a href="logout">Logout</a>
</body>
</html>