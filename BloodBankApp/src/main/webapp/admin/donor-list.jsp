<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User adminUser = (User) session.getAttribute("user");
    // Extra security check
    if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Donor List</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --brand-red: #c9302c;
            --brand-blue: #007bff;
            --light-gray: #f8f9fa;
            --medium-gray: #e9ecef;
            --text-color: #333;
        }
        * { box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--light-gray);
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
            background: #fff;
            padding: 20px 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 2px solid var(--medium-gray);
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        .header h1 {
            color: var(--brand-red);
            margin: 0;
        }
        .header a {
            text-decoration: none;
            color: var(--brand-red);
            font-weight: 600;
        }
        
        /* Table Styles */
        .admin-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .admin-table th, .admin-table td {
            border: 1px solid var(--medium-gray);
            padding: 12px 15px;
            text-align: left;
        }
        .admin-table th {
            background-color: #f4f4f4;
        }
        .admin-table tr:nth-child(even) {
            background-color: var(--light-gray);
        }
        .admin-table tr:hover {
            background-color: #f1f1f1;
        }
        .btn-view {
            background-color: var(--brand-blue);
            color: white;
            padding: 5px 10px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 0.9rem;
            font-weight: 600;
        }
        .btn-view:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Donor Management (CRM)</h1>
            <a href="${pageContext.request.contextPath}/admin.jsp">Back to Admin</a>
        </div>
        
        <p>This page lists all registered donors in the system. Click "View Profile" to see a complete history of their donations, requests, and earned badges.</p>

        <table class="admin-table">
            <thead>
                <tr>
                    <th>Donor ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Blood Group</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="donor" items="${donorList}">
                    <tr>
                        <td>${donor.id}</td>
                        <td>${donor.name}</td>
                        <td>${donor.email}</td>
                        <td>${donor.bloodGroup}</td>
                        <td>
                            <%-- This link won't work yet, but we'll build it in the next step --%>
                            <a href="${pageContext.request.contextPath}/admin/user-profile?id=${donor.id}" class="btn-view">View Profile</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>