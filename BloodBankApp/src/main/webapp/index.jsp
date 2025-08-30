<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to the Blood Bank</title>
    <style>
        body { font-family: sans-serif; text-align: center; padding-top: 50px; }
        h1 { color: #333; }
        .nav { margin-top: 30px; }
        .nav a { margin: 0 15px; text-decoration: none; padding: 12px 25px; border-radius: 5px; color: white; font-size: 16px; font-weight: bold; }
        .user-login { background-color: #007bff; }
        .hospital-login { background-color: #28a745; }
    </style>
</head>
<body>
    <h1>Welcome to the Blood Bank Application</h1>
    <p>Please select your login portal.</p>
    <div class="nav">
        <a href="login.jsp" class="user-login">User Login (Donor/Patient/Admin)</a>
        <a href="hospital-login.jsp" class="hospital-login">Hospital Login</a>
    </div>
</body>
</html>tml>