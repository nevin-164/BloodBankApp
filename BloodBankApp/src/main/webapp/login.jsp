<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>PLASMIC - User Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        /* Universal box-sizing for easier layout management */
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
            min-height: 100vh; /* Use min-height to handle content overflow */
            padding: 20px; /* Add padding for small screens */
        }

        .login-wrapper {
            display: flex;
            width: 100%;
            max-width: 900px; /* Set a max-width instead of a fixed width */
            height: 550px;
            box-shadow: 0 15px 30px rgba(0,0,0,0.1);
            border-radius: 15px;
            overflow: hidden;
            background: white;
        }

        .branding-side {
            background-color: #c9302c;
            color: white;
            width: 45%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding: 40px;
            text-align: center;
        }

        .branding-side h1 {
            font-size: 48px;
            margin: 0;
            font-weight: 600;
        }

        .branding-side p {
            font-size: 16px;
            font-weight: 300;
            margin-top: 10px;
        }

        .form-side {
            width: 55%;
            padding: 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .form-side h2 {
            margin-top: 0;
            margin-bottom: 20px;
            color: #333;
        }

        .input-group {
            margin-bottom: 15px;
        }

        .input-group label {
            display: block;
            margin-bottom: 5px;
            font-size: 14px;
            color: #555;
        }

        .input-group input, .input-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-family: 'Poppins', sans-serif;
        }

        button {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 5px;
            background-color: #007bff;
            color: white;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        button:hover {
            background-color: #0056b3;
        }

        .link-group {
            text-align: center;
            margin-top: 20px;
        }

        .link-group a {
            color: #007bff;
            text-decoration: none;
            margin: 0 10px;
        }

        /* --- Media Query for Mobile Devices --- */
        @media (max-width: 768px) {
            body {
                align-items: flex-start; /* Align to top on mobile */
            }

            .login-wrapper {
                flex-direction: column; /* Stack branding and form vertically */
                height: auto; /* Allow height to adjust to content */
                width: 100%;
                max-width: 450px; /* Constrain width on phones */
            }

            .branding-side, .form-side {
                width: 100%; /* Make both sections full-width */
            }

            .branding-side {
                padding: 40px 20px; /* Adjust padding for smaller screens */
            }

            .branding-side h1 {
                font-size: 36px; /* Slightly smaller title */
            }

            .form-side {
                padding: 30px 20px;
            }
        }

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