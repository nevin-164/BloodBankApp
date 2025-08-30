<!DOCTYPE html>
<html>
<head>
    <title>Add Hospital</title>
</head>
<body>
    <h2>Add Hospital</h2>
    <form action="${pageContext.request.contextPath}/AddHospitalServlet" method="post">
        <label>Name:</label><input type="text" name="name" required><br>
        <label>Email:</label><input type="email" name="email" required><br>
        <label>Password:</label><input type="password" name="password" required><br>
        <label>Contact Number:</label><input type="text" name="contactNumber" required><br>
        <label>Address:</label><input type="text" name="address" required><br>
        <button type="submit">Add Hospital</button>
    </form>
</body>
</html>