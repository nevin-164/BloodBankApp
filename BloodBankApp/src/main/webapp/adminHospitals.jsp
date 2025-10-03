<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>PLASMIC - Manage Hospitals</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
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

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 15px;
        }

        h2 {
            color: #333;
            margin: 0;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            background: white;
            border-radius: 8px;
            overflow: hidden;
        }

        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        tr:last-child td {
            border-bottom: none;
        }

        th {
            background-color: #343a40;
            color: white;
        }

        .actions-cell a {
            margin-right: 10px;
            text-decoration: none;
            font-weight: bold;
        }

        .edit-link { color: #007bff; }
        .delete-link { color: #dc3545; }

        .add-btn {
            background-color: #28a745;
            color: white;
            padding: 10px 15px;
            border-radius: 5px;
            text-decoration: none;
            font-weight: bold;
            white-space: nowrap;
        }

        .message {
            text-align: center;
            padding: 20px;
            font-weight: bold;
            color: #555;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .back-link {
            display: inline-block;
            margin-top: 20px;
            color: #007bff;
            text-decoration: none;
            font-weight: bold;
        }
    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />
    <div class="container">
        <div class="header">
            <h2><i class="fas fa-hospital"></i> Hospital Management</h2>
            <a href="${pageContext.request.contextPath}/addHospitalForm" class="add-btn"><i class="fas fa-plus"></i> Add New Hospital</a>
        </div>

        <c:if test="${not empty hospitals}">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Contact</th>
                        <th>Address</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="h" items="${hospitals}">
                        <tr>
                            <td>${h.id}</td>
                            <td>${h.name}</td>
                            <td>${h.email}</td>
                            <td>${h.contactNumber}</td>
                            <td>${h.address}</td>
                            <td class="actions-cell">
                                <a href="${pageContext.request.contextPath}/admin/hospitals/edit?hospitalId=${h.id}" class="edit-link"><i class="fas fa-edit"></i> Edit</a>
                                <a href="${pageContext.request.contextPath}/deleteHospital?hospitalId=${h.id}" 
                                   class="delete-link"
                                   onclick="return confirm('Are you sure you want to delete this hospital?');"><i class="fas fa-trash"></i> Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty hospitals}">
            <p class="message">No hospitals found.</p>
        </c:if>

        <a href="${pageContext.request.contextPath}/admin.jsp" class="back-link"><i class="fas fa-arrow-left"></i> Back to Dashboard</a>
    </div>
</body>
</html>