<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Manage Users</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { padding: 30px; }
        h2 { text-align: center; color: #333; margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); background: white; }
        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #007bff; color: white; }
        .back-link { display: inline-block; margin-top: 20px; color: #007bff; text-decoration: none; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Registered User Management</h2>

        <c:if test="${not empty users}">
            <table>
                <thead>
                    <tr>
                        <%-- ✅ FIXED: Removed Phone and Address columns --%>
                        <th>ID</th><th>Name</th><th>Email</th><th>Blood Group</th><th>Role</th><th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr>
                             <%-- ✅ FIXED: Removed Phone and Address columns --%>
                            <td>${user.id}</td><td>${user.name}</td><td>${user.email}</td><td>${user.bloodGroup}</td><td>${user.role}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/editUser?userId=${user.id}">Edit</a> |
                                <a href="${pageContext.request.contextPath}/admin/deleteUser?userId=${user.id}" onclick="return confirm('Are you sure?');">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty users}">
            <p>${message != null ? message : "No registered users were found."}</p>
        </c:if>
        
        <a href="${pageContext.request.contextPath}/admin.jsp" class="back-link">← Back to Dashboard</a>
    </div>
</body>
</html>