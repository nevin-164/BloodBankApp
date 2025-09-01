<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>PLASMIC - User Login</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; margin: 0; background-color: #f4f7f6; display: flex; justify-content: center; align-items: center; height: 100vh; }
        .login-wrapper { display: flex; width: 800px; height: 550px; box-shadow: 0 15px 30px rgba(0,0,0,0.1); border-radius: 15px; overflow: hidden; background: white; }
        .branding-side { background-color: #c9302c; color: white; width: 45%; display: flex; flex-direction: column; justify-content: center; align-items: center; padding: 40px; text-align: center; }
        .branding-side h1 { font-size: 48px; margin: 0; font-weight: 600; }
        .branding-side p { font-size: 16px; font-weight: 300; margin-top: 10px; }
        .form-side { width: 55%; padding: 40px; display: flex; flex-direction: column; justify-content: center; }
        .form-side h2 { margin-top: 0; margin-bottom: 20px; color: #333; }
        .input-group { margin-bottom: 15px; }
        .input-group label { display: block; margin-bottom: 5px; font-size: 14px; color: #555; }
        .input-group input, .input-group select { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; box-sizing: border-box; font-family: 'Poppins', sans-serif; }
        button { width: 100%; padding: 12px; border: none; border-radius: 5px; background-color: #007bff; color: white; font-size: 16px; font-weight: 600; cursor: pointer; }
        .link-group { text-align: center; margin-top: 20px; }
        .link-group a { color: #007bff; text-decoration: none; margin: 0 10px; }
    </style>
</head>
<body>
    <div class="login-wrapper">
        <div class="branding-side">
            <h1>PLASMIC</h1>
            <p>Your journey to saving lives starts here.</p>
        </div>
        <div class="form-side">
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
                <button type="submit">Login</button>
            </form>
            <div class="link-group">
                <a href="register.jsp">Register</a>
                <a href="index.jsp">Home</a>
            </div>
        </div>
    </div>
</body>
</html>