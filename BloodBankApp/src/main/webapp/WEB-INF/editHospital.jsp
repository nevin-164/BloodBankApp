<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>PLASMIC - Edit Hospital</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* ✅ NEW: Universal box-sizing and a modern font stack */
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
            padding: 20px; /* Adds spacing on mobile */
        }
        
        /* ✅ UPDATED: Container is now fluid */
        .form-container {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 450px; /* Set a max-width instead of a fixed width */
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
            font-size: 16px; /* ✅ UPDATED: Prevents auto-zoom on iOS */
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
        <h2>Edit Hospital Details</h2>
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

            <button type="submit">Update Hospital</button>
        </form>

        <div class="link-group">
             <a href="${pageContext.request.contextPath}/admin/hospitals">← Back to Hospital List</a>
        </div>
    </div>
</body>
</html>