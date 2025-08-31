<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Registration</title>
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f4f4f4; padding: 20px 0; }
        .register-container { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 350px; }
        h2 { text-align: center; color: #333; }
        .input-group { margin-bottom: 1rem; }
        label { display: block; margin-bottom: 5px; color: #555; }
        input[type="text"], input[type="email"], input[type="password"], select {
            width: 100%;
            box-sizing: border-box;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #28a745; /* Green color for registration */
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .error-message { color: #d9534f; text-align: center; margin-top: 10px; font-weight: bold; }
        .link-group { text-align: center; margin-top: 15px; }
    </style>
</head>
<body>
    <div class="register-container">
        <h2>Create an Account</h2>
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
            
            <%-- ✅ ADDED: Contact Number Field --%>
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
                    <option value="A+">A+</option> <option value="A-">A-</option>
                    <option value="B+">B+</option> <option value="B-">B-</option>
                    <option value="AB+">AB+</option> <option value="AB-">AB-</option>
                    <option value="O+">O+</option> <option value="O-">O-</option>
                </select>
            </div>
            <button type="submit">Register</button>
        </form>

        <c:if test="${not empty msg}">
            <p class="error-message">${msg}</p>
        </c:if>

        <div class="link-group">
            <p>Already have an account? <a href="login.jsp">Login here</a></p>
        </div>
    </div>
    <script>
        function toggleBloodGroupField() {
            var role = document.getElementById('role').value;
            var bloodGroupField = document.getElementById('blood-group-field');
            if (role === 'DONOR') {
                bloodGroupField.style.display = 'block';
            } else {
                bloodGroupField.style.display = 'none';
            }
        }
        toggleBloodGroupField();
    </script>
</body>
</html>