<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>PLASMIC - Manage Users</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { padding: 30px; }
        h2, h3 { text-align: center; color: #333; margin-bottom: 20px; }
        h3 { color: #555; border-top: 2px solid #eee; padding-top: 20px; margin-top: 30px; }
        table { width: 100%; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); background: white; }
        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #007bff; color: white; }
        .donors-header { background-color: #28a745; } /* Green for donors */
        .patients-header { background-color: #17a2b8; } /* Teal for patients */
        .message { text-align: center; padding: 20px; font-weight: bold; color: #555; }
        .back-link { display: block; text-align: center; margin-top: 30px; color: #007bff; text-decoration: none; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Registered User Management</h2>

        <%-- ✅ ADDED: Section for Donors --%>
        <h3>Donors</h3>
        <c:if test="${not empty donors}">
            <table>
                <thead class="donors-header">
                    <tr><th>ID</th><th>Name</th><th>Email</th><th>Blood Group</th><th>Actions</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${donors}">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.email}</td>
                            <td>${user.bloodGroup}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/editUser?userId=${user.id}">Edit</a> |
                                <a href="${pageContext.request.contextPath}/admin/deleteUser?userId=${user.id}" onclick="return confirm('Are you sure?');">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty donors}">
            <p class="message">No registered donors found.</p>
        </c:if>

        <%-- ✅ ADDED: Section for Patients --%>
        <h3>Patients</h3>
        <c:if test="${not empty patients}">
            <table>
                <thead class="patients-header">
                    <tr><th>ID</th><th>Name</th><th>Email</th><th>Blood Group</th><th>Actions</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${patients}">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.email}</td>
                            <td>${user.bloodGroup}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/editUser?userId=${user.id}">Edit</a> |
                                <a href="${pageContext.request.contextPath}/admin/deleteUser?userId=${user.id}" onclick="return confirm('Are you sure?');">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty patients}">
            <p class="message">No registered patients found.</p>
        </c:if>
        
        <a href="${pageContext.request.contextPath}/admin.jsp" class="back-link">← Back to Dashboard</a>
    </div>
</body>
</html>