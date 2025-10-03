<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>PLASMIC - Manage Hospitals</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* --- Base & Desktop Styles --- */
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
            flex-wrap: wrap; /* Allow wrapping on medium screens */
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
            overflow: hidden; /* Ensures radius is applied to corners */
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
            background-color: #17a2b8;
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
        
        /* --- ✅ NEW: Media Query for Mobile Devices --- */
        @media (max-width: 768px) {
            .container {
                padding: 15px;
            }
            .header {
                flex-direction: column;
                align-items: flex-start;
            }
            
            /* Responsive Table Styling */
            table, thead, tbody, th, td, tr {
                display: block;
            }
            
            table {
                box-shadow: none;
                background: none;
            }

            thead tr {
                position: absolute;
                top: -9999px;
                left: -9999px;
            }
            
            tr {
                border: 1px solid #ddd;
                border-radius: 5px;
                margin-bottom: 15px;
                background: #fff;
                padding: 10px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            }
            
            td {
                border: none;
                border-bottom: 1px solid #eee;
                position: relative;
                padding-left: 50%;
                text-align: right;
                min-height: 30px; /* For consistent alignment */
            }
            
            td:last-child {
                border-bottom: 0;
            }
            
            td:before {
                content: attr(data-label); /* Use the data-label attribute as the label */
                position: absolute;
                left: 10px;
                width: 45%;
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
<jsp:include page="common/notification.jsp" />
    <div class="container">
        <div class="header">
            <h2>Hospital Management</h2>
            <a href="${pageContext.request.contextPath}/addHospitalForm" class="add-btn">+ Add New Hospital</a>
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
                            <td data-label="ID">${h.id}</td>
                            <td data-label="Name">${h.name}</td>
                            <td data-label="Email">${h.email}</td>
                            <td data-label="Contact">${h.contactNumber}</td>
                            <td data-label="Address">${h.address}</td>
                            <td data-label="Actions" class="actions-cell">
                                <a href="${pageContext.request.contextPath}/admin/hospitals/edit?hospitalId=${h.id}" class="edit-link">Edit</a>
                                <a href="${pageContext.request.contextPath}/deleteHospital?hospitalId=${h.id}" 
                                   class="delete-link"
                                   onclick="return confirm('Are you sure you want to delete this hospital?');">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty hospitals}">
            <p class="message">No hospitals found.</p>
        </c:if>

        <a href="${pageContext.request.contextPath}/admin.jsp" class="back-link">← Back to Dashboard</a>
    </div>
</body>
</html>