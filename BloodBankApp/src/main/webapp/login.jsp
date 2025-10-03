<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>PLASMIC - User Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: 'Poppins', sans-serif;
            margin: 0;
            background-color: #f4f7f6;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }

        .login-container {
            display: flex;
            width: 100%;
            max-width: 900px;
            min-height: 550px;
            box-shadow: 0 15px 30px rgba(0,0,0,0.1);
            border-radius: 15px;
            overflow: hidden;
            background: white;
        }

        .login-branding {
            background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            width: 45%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding: 40px;
            text-align: center;
        }

        .login-branding h1 {
            font-size: 48px;
            margin: 0;
            font-weight: 600;
        }

        .login-branding p {
            font-size: 16px;
            font-weight: 300;
            margin-top: 10px;
        }

        .login-form {
            width: 55%;
            padding: 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .login-form h2 {
            margin-top: 0;
            margin-bottom: 20px;
            color: #333;
        }

        .input-group {
            margin-bottom: 15px;
            position: relative;
        }

        .input-group label {
            display: block;
            margin-bottom: 5px;
            font-size: 14px;
            color: #555;
        }

        .input-group input {
            width: 100%;
            padding: 10px 10px 10px 40px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-family: 'Poppins', sans-serif;
        }

        .input-group .input-icon {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #aaa;
        }

        button {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 5px;
            background-image: linear-gradient(to right, #6a11cb 0%, #2575fc 100%);
            color: white;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        button:hover {
            background-color: #218838;
        }

        .links {
            text-align: center;
            margin-top: 20px;
        }

        .links a {
            color: #007bff;
            text-decoration: none;
            margin: 0 10px;
        }

        @media (max-width: 768px) {
            body {
                align-items: flex-start;
            }

            .login-container {
                flex-direction: column;
                height: auto;
                min-height: 0;
                width: 100%;
                max-width: 450px;
            }

            .login-branding, .login-form {
                width: 100%;
            }
            
            .login-branding {
                padding: 40px 20px;
            }
            
            .login-branding h1 {
                font-size: 36px;
            }

            .login-form {
                padding: 30px 20px;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="common/notification.jsp" />
    <div class="login-container">
        <div class="login-branding">
            <h1>PLASMIC</h1>
            <p>Your connection to saving lives.</p>
        </div>
        <div class="login-form">
            <h2>User Login</h2>
            <form action="login" method="post">
                <div class="input-group">
                    <label for="email">Email:</label>
                    <i class="fas fa-envelope input-icon"></i>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="input-group">
                    <label for="password">Password:</label>
                    <i class="fas fa-lock input-icon"></i>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit">Login</button>
            </form>
            <div class="links">
                <a href="register.jsp">Create an account</a> | <a href="index.jsp">Back to Home</a>
            </div>
        </div>
    </div>
</body>
</html>