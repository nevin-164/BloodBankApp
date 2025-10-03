<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>PLASMIC - Edit Hospital</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        * {
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }
        
        .form-container {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 450px;
        }
        
        h2 {
            text-align: center;
            color: #333;
            margin-top: 0;
        }
        
        .input-group {
            margin-bottom: 1rem;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: bold;
        }
        
        input[type="text"], input[type="email"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 16px;
        }
        
        button {
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            transition: background-color 0.2s;
        }
        
        button:hover {
            background-color: #0056b3;
        }
        
        .link-group {
            text-align: center;
            margin-top: 15px;
        }
        
        .link-group a {
            color: #337ab7;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h2><i class="fas fa-edit"></i> Edit Hospital Details</h2>
        <form action="${pageContext.request.contextPath}/admin/hospitals/edit" method="post">
            
            <input type="hidden" name="hospitalId" value="${hospital.hospitalId}" />

            <div class="input-group">
                <label for="name">Name:</label>
           
                 <input type="text" id="name" name="name" value="${hospital.name}" required />
            </div>
            <div class="input-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="${hospital.email}" required />
            </div>
            <div class="input-group">
                <label for="contactNumber">Contact:</label>
                <input type="text" id="contactNumber" name="contactNumber" value="${hospital.contactNumber}" required />
            </div>
            <div class="input-group">
                <label for="address">Address:</label>
                <input type="text" id="address" name="address" value="${hospital.address}" required />
            </div>

            <button type="submit"><i class="fas fa-save"></i> Update Hospital</button>
        </form>

        <div class="link-group">
             <a href="${pageContext.request.contextPath}/admin/hospitals"><i class="fas fa-arrow-left"></i> Back to Hospital List</a>
        </div>
    </div>
</body>
</html>