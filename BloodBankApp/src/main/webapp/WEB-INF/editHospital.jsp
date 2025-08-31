<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>PLASMIC - Edit Hospital</title>
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f4f4f4; }
        .form-container { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 350px; }
        h2 { text-align: center; color: #333; }
        .input-group { margin-bottom: 1rem; }
        label { display: block; margin-bottom: 5px; color: #555; }
        input[type="text"], input[type="email"] {
            width: 100%;
            box-sizing: border-box;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #007bff; /* Blue for "Update" */
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .link-group { text-align: center; margin-top: 15px; }
        .link-group a { color: #337ab7; text-decoration: none; }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Edit Hospital Details</h2>
        <form action="${pageContext.request.contextPath}/admin/hospitals/edit" method="post">
            
            <input type="hidden" name="hospitalId" value="${hospital.hospitalId}" />

            <div class="input-group">
                <label>Name:</label>
                <input type="text" name="name" value="${hospital.name}" required />
            </div>
            <div class="input-group">
                <label>Email:</label>
                <input type="email" name="email" value="${hospital.email}" required />
            </div>
            <div class="input-group">
                <label>Contact:</label>
                <input type="text" name="contactNumber" value="${hospital.contactNumber}" required />
            </div>
            <div class="input-group">
                <label>Address:</label>
                <input type="text" name="address" value="${hospital.address}" required />
            </div>

            <button type="submit">Update Hospital</button>
        </form>

        <div class="link-group">
             <a href="${pageContext.request.contextPath}/admin/hospitals">← Back to Hospital List</a>
        </div>
    </div>
</body>
</html>