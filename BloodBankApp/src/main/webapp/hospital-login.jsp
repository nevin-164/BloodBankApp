<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>PLASMIC - Hospital Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
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

        .login-wrapper {
            display: flex;
            width: 100%;
            max-width: 900px;
            min-height: 550px;
            box-shadow: 0 15px 30px rgba(0,0,0,0.1);
            border-radius: 15px;
            overflow: hidden;
            background: white;
        }

        .branding-side {
            background-image: linear-gradient(to top, #09203f 0%, #537895 100%);
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
            background-image: linear-gradient(to right, #25aae1, #40e495, #30dd8a, #2bb673);
            box-shadow: 0 4px 15px 0 rgba(49, 196, 190, 0.75);
            color: white;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        button:hover {
            background-color: #218838;
        }

        .link-group {
            text-align: center;
            margin-top: 20px;
        }

        .link-group a {
            color: #007bff;
            text-decoration: none;
        }

        @media (max-width: 768px) {
            body {
                align-items: flex-start;
            }

            .login-wrapper {
                flex-direction: column;
                height: auto;
                min-height: 0;
                width: 100%;
                max-width: 450px;
            }

            .branding-side, .form-side {
                width: 100%;
            }
            
            .branding-side {
                padding: 40px 20px;
            }
            
            .branding-side h1 {
                font-size: 36px;
            }

            .form-side {
                padding: 30px 20px;
            }
        }
    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />
    <div class="login-wrapper">
        <div class="branding-side">
            <h1>PLASMIC</h1>
            <p>Managing the gift of life, efficiently.</p>
        </div>
        <div class="form-side">
            <h2>Hospital Portal Login</h2>
            
            <form action="${pageContext.request.contextPath}/hospital-login" method="post">
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
            <div class="link-group">
                 <a href="index.jsp">Back to Home</a>
            </div>
        </div>
    </div>
</body>
</html>