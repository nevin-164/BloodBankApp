<%@ page import="model.User" %>
<%
    User u = (User) request.getAttribute("user");
%>
<html>
<head>
    <title>Edit User</title>
</head>
<body>
    <h2>Edit User</h2>
    <form action="editUser" method="post">
        <input type="hidden" name="userId" value="<%= u.getUserId() %>"/>
        <p>Name: <input type="text" name="name" value="<%= u.getName() %>" required/></p>
        <p>Email: <input type="email" name="email" value="<%= u.getEmail() %>" required/></p>
        <p>Blood Group: <input type="text" name="bloodGroup" value="<%= u.getBloodGroup() %>" required/></p>
        <p><input type="submit" value="Update User"/></p>
    </form>
    <a href="<%= request.getContextPath() %>/admin/users">Back to Users</a>
</body>
</html>
