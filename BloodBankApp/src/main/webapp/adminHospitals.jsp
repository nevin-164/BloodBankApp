<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Manage Hospitals</title>
    <style>
        table {
            width: 80%;
            border-collapse: collapse;
            margin: 20px auto;
        }
        th, td {
            border: 1px solid #ccc;
            padding: 10px;
            text-align: center;
        }
        th {
            background: #f2f2f2;
        }
        a.button {
            padding: 6px 12px;
            text-decoration: none;
            border-radius: 5px;
            color: #fff;
        }
        .add-btn { background: #28a745; }
        .edit-btn { background: #007bff; }
        .delete-btn { background: #dc3545; }
        .top-actions {
            width: 80%;
            margin: 20px auto;
            text-align: right;
        }
    </style>
</head>
<body>
    <h2 style="text-align:center;">Hospital Management</h2>

    <div class="top-actions">
        <!-- + Add Hospital button -->
        <a href="${pageContext.request.contextPath}/addHospitalForm" class="button add-btn">+ Add Hospital</a>
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
                        <td>
                            <!-- Edit button -->
                            <a href="editHospital?hospitalId=${h.hospitalId}">Edit</a>
                            
                            <!-- Delete button -->
                            <a href="${pageContext.request.contextPath}/deleteHospital?hospitalId=${h.id}" 
                               class="button delete-btn"
                               onclick="return confirm('Are you sure you want to delete this hospital?');">🗑️ Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <c:if test="${empty hospitals}">
        <p style="text-align:center;">No hospitals found.</p>
    </c:if>
</body>
</html>

