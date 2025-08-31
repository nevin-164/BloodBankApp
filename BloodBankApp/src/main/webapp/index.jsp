<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to PLASMIC</title>
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            text-align: center;
            padding-top: 50px;
            background-color: #f8f9fa;
            margin: 0;
        }
        h1 {
            color: #c9302c; /* Thematic red color */
            font-weight: 600;
            font-size: 48px;
        }
        p {
            color: #6c757d;
            font-size: 18px;
        }
        .nav {
            margin-top: 40px;
            display: flex;
            justify-content: center;
            gap: 20px; /* Adds space between the buttons */
        }
        .nav a {
            text-decoration: none;
            padding: 15px 30px;
            border-radius: 8px;
            color: white;
            font-size: 16px;
            font-weight: 600;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .nav a:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        .user-login {
            background-color: #007bff; /* Blue for users */
        }
        .hospital-login {
            background-color: #28a745; /* Green for hospitals */
        }
    </style>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
</head>
<body>
    <h1>Welcome to PLASMIC</h1>
    <p>Your integrated blood bank management solution.</p>

    <div class="nav">
        <%-- This link directs to the general user login page --%>
        <a href="login.jsp" class="user-login">User Portal (Donor/Patient/Admin)</a>
        
        <%-- ✅ FIXED: The missing hospital login link is back --%>
        <a href="hospital-login.jsp" class="hospital-login">Hospital Portal</a>
    </div>
</body>
</html>