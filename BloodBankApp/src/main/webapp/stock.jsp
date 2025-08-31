<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map, dao.StockDAO" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Fetch the current stock levels from the DAO
    Map<String, Integer> currentStock = StockDAO.getAllStock();
    request.setAttribute("currentStock", currentStock);
%>
<html>
<head>
    <title>PLASMIC - Blood Stock Levels</title>
    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f8f9fa; }
        .container { padding: 30px; }
        h2 { text-align: center; color: #c9302c; margin-bottom: 20px; } /* Red Theme */
        table {
            width: 60%; /* Made the table a bit narrower for better focus */
            margin: auto;
            border-collapse: collapse;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            background: white;
        }
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #dc3545; /* Red Theme */
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #007bff;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Current Blood Stock Inventory</h2>

        <c:if test="${not empty currentStock}">
            <table>
                <thead>
                    <tr>
                        <th>Blood Group</th>
                        <th>Units Available</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="entry" items="${currentStock}">
                        <tr>
                            <td>${entry.key}</td>
                            <td>${entry.value}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty currentStock}">
            <p style="text-align:center;">Could not retrieve stock levels at this time.</p>
        </c:if>

        <a href="admin.jsp" class="back-link">← Back to Admin Dashboard</a>
    </div>
</body>
</html>