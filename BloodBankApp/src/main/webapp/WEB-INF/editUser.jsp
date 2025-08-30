<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Edit User</title>
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f4f4f4; }
        .edit-container { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 350px; }
        h2 { text-align: center; color: #333; }
        .input-group { margin-bottom: 1rem; }
        label { display: block; margin-bottom: 5px; color: #555; }
        input[type="text"], input[type="email"], select {
            width: 100%;
            box-sizing: border-box;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #007bff; /* Blue for editing */
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover { background-color: #0056b3; }
    </style>
</head>
<body>
    <div class="edit-container">
        <h2>Edit User Details</h2>
        <form action="${pageContext.request.contextPath}/admin/editUser" method="post">
            
            <%-- ✅ FIXED: Use the correct user.id property --%>
            <input type="hidden" name="userId" value="${user.id}" />

            <div class="input-group">
                <label for="name">Full Name:</label>
                <input type="text" id="name" name="name" value="${user.name}" required>
            </div>
            <div class="input-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="${user.email}" required>
            </div>
            <div class="input-group">
                <label for="bloodGroup">Blood Group:</label>
                <input type="text" id="bloodGroup" name="bloodGroup" value="${user.bloodGroup}" required>
            </div>
            <button type="submit">Update User</button>
        </form>
    </div>
</body>
</html>