<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Hospital</title>
</head>
<body>
    <h2>Edit Hospital</h2>
    <form action="${pageContext.request.contextPath}/admin/hospitals/edit" method="post">
        <input type="hidden" name="hospitalId" value="${hospital.hospitalId}" />

        <label>Name:</label>
        <input type="text" name="name" value="${hospital.name}" required /><br/>

        <label>Email:</label>
        <input type="email" name="email" value="${hospital.email}" required /><br/>

        <label>Contact:</label>
        <input type="text" name="contactNumber" value="${hospital.contactNumber}" required /><br/>

        <label>Address:</label>
        <input type="text" name="address" value="${hospital.address}" required /><br/>

        <button type="submit">Update Hospital</button>
    </form>
</body>
</html>