<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Expiry Alerts</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { padding: 30px; }
        h2 { text-align: center; color: #dc3545; margin-bottom: 20px; }
        table { width: 80%; margin: auto; border-collapse: collapse; box-shadow: 0 2px 4px rgba(0,0,0,0.1); background: white; }
        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #dc3545; color: white; }
        .message { text-align: center; padding: 20px; font-weight: bold; color: #555; }
        .back-link { display: block; text-align: center; margin-top: 20px; color: #007bff; text-decoration: none; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Blood Stock Expiring Soon (Next 7 Days)</h2>

        <c:if test="${not empty expiring}">
            <table>
                <thead>
                    <tr>
                        <th>Blood Group</th>
                        <th>Units Expiring</th>
                        <th>Expiry Date</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="donation" items="${expiring}">
                        <tr>
                            <td>${donation[0]}</td>
                            <td>${donation[1]}</td>
                            <td>${donation[2]}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty expiring}">
            <p class="message">No blood units are expiring in the next 7 days.</p>
        </c:if>

        <a href="admin.jsp" class="back-link">← Back to Dashboard</a>
    </div>
</body>
</html>