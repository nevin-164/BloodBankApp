<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
<body>
<h3>Register</h3>
<form action="register" method="post">
  Name: <input name="name" required><br>
  Email: <input type="email" name="email" required><br>
  Password: <input type="password" name="password" required><br>
  Role:
  <select name="role">
    <option>DONOR</option>
    <option>PATIENT</option>
  </select><br>
  Blood Group (if donor): <input name="blood_group"><br>
  <button type="submit">Register</button>
</form>

</body>
</html>