<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Donate Blood</title>
</head>
<body>
    <h2>Blood Donation Form</h2>

    <!-- Show message if servlet sets it -->
    <c:if test="${not empty msg}">
        <p style="color:red;">${msg}</p>
    </c:if>

    <form action="donate" method="post">
        <!-- Hidden donor id (should be passed from session or prefilled) -->
        <input type="hidden" name="user_id" value="${sessionScope.user.userId}" />

        <label>Blood Group:</label>
        <select name="blood_group" required>
            <option value="">--Select--</option>
            <option value="A+">A+</option>
            <option value="A-">A-</option>
            <option value="B+">B+</option>
            <option value="B-">B-</option>
            <option value="O+">O+</option>
            <option value="O-">O-</option>
            <option value="AB+">AB+</option>
            <option value="AB-">AB-</option>
        </select><br/>

        <label>Units:</label>
        <input type="number" name="units" min="1" required /><br/>

        <label>Select Hospital:</label>
        <select name="hospital_id" required>
            <option value="">--Select Hospital--</option>
            <c:forEach var="hospital" items="${hospitals}">
                <option value="${hospital.hospitalId}">${hospital.name}</option>
            </c:forEach>
        </select><br/>

        <button type="submit">Donate</button>
    </form>
</body>
</html>
