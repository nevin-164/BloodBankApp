<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Login</title>
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f4; }
        .login-container { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 300px; }
        h2 { text-align: center; color: #333; }
        .input-group { margin-bottom: 1rem; }
        label { display: block; margin-bottom: 5px; color: #555; }
        input[type="email"], input[type="password"], select {
            width: 100%;
            box-sizing: border-box;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #007bff; /* Blue color for user login */
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover { background-color: #0056b3; }
        .error-message { color: #d9534f; text-align: center; margin-top: 10px; font-weight: bold; }
        .success-message { color: #28a745; text-align: center; margin-top: 10px; font-weight: bold; }
        .link-group { text-align: center; margin-top: 15px; }
        .link-group a { color: #337ab7; text-decoration: none; margin: 0 10px; }
    </style>
</head>
<body>
    <div class="login-container">
        <h2>User Portal Login</h2>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="input-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="input-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="input-group">
                <label for="role-select">I am a:</label>
                <select name="role" id="role-select" required>
                    <option value="DONOR">Donor</option>
                    <option value="PATIENT">Patient</option>
                    <option value="ADMIN">Admin</option>
                </select>
            </div>
            <button type="submit">Login</button>
        </form>
        
        <%-- Display error or success messages --%>
        <c:if test="${not empty msg}">
            <p class="error-message">${msg}</p>
        </c:if>
        <c:if test="${not empty param.success}">
            <p class="success-message">${param.success}</p>
        </c:if>
        
        <div class="link-group">
            <a href="register.jsp">Register</a>
            <a href="index.jsp">Home</a>
        </div>
    </div>
</body>
</html>