<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Assuming 'donors' and 'patients' are set by a servlet --%>
<html>
<head>
    <title>PLASMIC - Manage Users</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* ✅ NEW: Universal box-sizing and a modern font stack */
        * {
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            background-color: #f8f9fa;
        }
        
        .container {
            padding: 30px;
        }
        
        h2, h3 {
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }
        
        h3 {
            color: #555;
            border-top: 2px solid #eee;
            padding-top: 20px;
            margin-top: 30px;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            background: white;
            border-radius: 8px; /* Added for a modern look */
            overflow: hidden; /* Ensures radius applies correctly */
        }
        
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        tbody tr:last-child td {
            border-bottom: none;
        }
        
        th {
            color: white;
        }
        
        .donors-header { background-color: #28a745; }
        .patients-header { background-color: #17a2b8; }
        
        .message {
            text-align: center;
            padding: 20px;
            font-weight: bold;
            color: #555;
        }
        
        .back-link {
            display: block;
            text-align: center;
            margin-top: 30px;
            color: #007bff;
            text-decoration: none;
            font-weight: bold;
        }

        /* --- ✅ NEW: Media Query for Mobile Devices --- */
        @media (max-width: 768px) {
            .container {
                padding: 15px;
            }
            h2 { font-size: 1.75rem; }
            h3 { font-size: 1.25rem; }

            /* Responsive Table Styling */
            table, thead, tbody, th, td, tr {
                display: block;
            }
            thead tr {
                position: absolute; /* Hide original headers */
                top: -9999px;
                left: -9999px;
            }
            table {
                box-shadow: none;
                background: none;
                border-radius: 0;
            }
            tr {
                border: 1px solid #ccc;
                margin-bottom: 15px;
                border-radius: 8px;
                background: white;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                padding: 10px;
            }
            td {
                border: none;
                border-bottom: 1px solid #eee;
                position: relative;
                padding-left: 40%;
                text-align: right;
                min-height: 40px;
                display: flex;
                align-items: center;
                justify-content: flex-end;
            }
            td:last-child {
                border-bottom: 0;
                justify-content: flex-start; /* Align actions to the left */
                padding-left: 10px;
            }
            td:before {
                content: attr(data-label); /* Use data-label for the new "header" */
                position: absolute;
                left: 10px;
                width: 35%;
                padding-right: 10px;
                white-space: nowrap;
                text-align: left;
                font-weight: bold;
                color: #333;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Registered User Management</h2>

        <h3>Donors</h3>
        <c:if test="${not empty donors}">
            <table>
                <thead class="donors-header">
                    <tr><th>ID</th><th>Name</th><th>Email</th><th>Blood Group</th><th>Actions</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${donors}">
                        <tr>
                            <td data-label="ID">${user.id}</td>
                            <td data-label="Name">${user.name}</td>
                            <td data-label="Email">${user.email}</td>
                            <td data-label="Blood Group">${user.bloodGroup}</td>
                            <td data-label="Actions">
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

        <h3>Patients</h3>
        <c:if test="${not empty patients}">
            <table>
                <thead class="patients-header">
                    <tr><th>ID</th><th>Name</th><th>Email</th><th>Blood Group</th><th>Actions</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${patients}">
                        <tr>
                            <td data-label="ID">${user.id}</td>
                            <td data-label="Name">${user.name}</td>
                            <td data-label="Email">${user.email}</td>
                            <td data-label="Blood Group">${user.bloodGroup}</td>
                            <td data-label="Actions">
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