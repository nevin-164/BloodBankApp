<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>PLASMIC - User Registration</title>
    <!-- ✅ NEW: Viewport meta tag for responsive design -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        /* ✅ NEW: Universal box-sizing for predictable layouts */
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
            padding: 20px; /* ✅ UPDATED: Padding on body for mobile spacing */
        }
        
        /* ✅ UPDATED: Container is now fluid and responsive */
        .register-container {
            background: white;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 15px 30px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 450px; /* Constrains the width on larger screens */
        }
        
        h2 {
            text-align: center;
            color: #333;
            margin-top: 0;
        }
        
        .input-group {
            margin-bottom: 15px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            font-size: 14px;
            color: #555;
        }
        
        input, select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-family: 'Poppins', sans-serif;
            font-size: 16px; /* Prevents auto-zoom on iOS */
        }
        
        button {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 5px;
            background-color: #28a745;
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
    </style>
</head>
<body>
    <div class="register-container">
        <h2>Create a PLASMIC Account</h2>
        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="input-group">
                <label for="name">Full Name:</label>
                <input type="text" id="name" name="name" required>
            </div>
            <div class="input-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="input-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="input-group">
                <label for="contact_number">Contact Number:</label>
                <input type="text" id="contact_number" name="contact_number" required>
            </div>
            <div class="input-group">
                <label for="role">Register as a:</label>
                <select id="role" name="role" onchange="toggleBloodGroupField()" required>
                    <option value="DONOR">Donor</option>
                    <option value="PATIENT">Patient</option>
                </select>
            </div>
            <div class="input-group" id="blood-group-field">
                <label for="blood_group">Blood Group:</label>
                <select id="blood_group" name="blood_group">
                    <option value="A+">A+</option><option value="A-">A-</option>
                    <option value="B+">B+</option><option value="B-">B-</option>
                    <option value="AB+">AB+</option><option value="AB-">AB-</option>
                    <option value="O+">O+</option><option value="O-">O-</option>
                </select>
            </div>
            <button type="submit">Register</button>
        </form>
        <div class="link-group">
            <p>Already have an account? <a href="login.jsp">Login here</a></p>
        </div>
    </div>
    <script>
        function toggleBloodGroupField() {
            var bloodGroupField = document.getElementById('blood-group-field');
            var roleSelect = document.getElementById('role');
            bloodGroupField.style.display = (roleSelect.value === 'DONOR') ? 'block' : 'none';
        }
        // Run on page load
        toggleBloodGroupField();
    </script>
</body>
</html>