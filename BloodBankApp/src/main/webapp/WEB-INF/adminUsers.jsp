<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Admin - User Management</title>
</head>
<body>
    <h2>Registered Users</h2>

    <c:if test="${not empty errorMessage}">
        <p style="color:red">${errorMessage}</p>
    </c:if>

    <c:if test="${empty users}">
        <p>No users found.</p>
    </c:if>

    <c:if test="${not empty users}">
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Blood Group</th>
            </tr>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td>${u.userId}</td>
                    <td>${u.name}</td>
                    <td>${u.email}</td>
                    <td>${u.bloodGroup}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</body>
</html>
