<!DOCTYPE html>
<html>
<head>
    <title>PLASMIC - Add Hospital</title>
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f4f4f4; }
        .form-container { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 350px; }
        h2 { text-align: center; color: #333; }
        .input-group { margin-bottom: 1rem; }
        label { display: block; margin-bottom: 5px; color: #555; }
        input[type="text"], input[type="email"], input[type="password"] {
            width: 100%;
            box-sizing: border-box;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #28a745; /* Green for "Add" */
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .link-group { text-align: center; margin-top: 15px; }
        .link-group a { color: #337ab7; text-decoration: none; }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Add New Hospital</h2>
        <form action="${pageContext.request.contextPath}/AddHospitalServlet" method="post">
            <div class="input-group">
                <label>Name:</label>
                <input type="text" name="name" required>
            </div>
            <div class="input-group">
                <label>Email:</label>
                <input type="email" name="email" required>
            </div>
            <div class="input-group">
                <label>Password:</label>
                <input type="password" name="password" required>
            </div>
            <div class="input-group">
                <label>Contact Number:</label>
                <input type="text" name="contactNumber" required>
            </div>
            <div class="input-group">
                <label>Address:</label>
                <input type="text" name="address" required>
            </div>
            <button type="submit">Add Hospital</button>
        </form>

        <%-- ✅ ADDED: The "Back to Dashboard" link --%>
        <div class="link-group">
             <a href="${pageContext.request.contextPath}/admin.jsp">← Back to Dashboard</a>
        </div>
    </div>
</body>
</html>