<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Assuming the 'expiring' attribute is set by a servlet --%>
<html>
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>Expiry Alerts</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* ✅ NEW: Universal box-sizing and a modern font stack */
        * {
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            background-color: #f8f9fa;
        }
        
        .container {
            padding: 30px;
        }
        
        h2 {
            text-align: center;
            color: #dc3545;
            margin-bottom: 20px;
        }
        
        /* ✅ UPDATED: Table is now more flexible */
        table {
            width: 100%;
            max-width: 800px; /* Constrain on large screens */
            margin: auto;
            border-collapse: collapse;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            background: white;
            border-radius: 8px; /* Added for a softer look */
            overflow: hidden; /* Ensures radius is applied to corners */
        }
        
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            background-color: #dc3545;
            color: white;
        }

        /* Remove bottom border from the last row for a cleaner look */
        tbody tr:last-child td {
            border-bottom: none;
        }
        
        .message {
            text-align: center;
            padding: 20px;
            font-weight: bold;
            color: #555;
            background-color: #fff;
            border-radius: 8px;
            max-width: 800px;
            margin: auto;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #007bff;
            text-decoration: none;
            font-weight: bold;
        }

        /* --- ✅ NEW: Media Query for Mobile Devices --- */
        @media (max-width: 768px) {
            .container {
                padding: 15px;
            }
            h2 {
                font-size: 1.5rem;
            }

            /* Responsive Table Styling */
            table, thead, tbody, th, td, tr {
                display: block;
            }
            thead tr {
                position: absolute; /* Hide original headers */
                top: -9999px;
                left: -9999px;
            }
            table {
                box-shadow: none;
                background: none;
            }
            tr {
                border: 1px solid #ccc;
                margin-bottom: 15px;
                border-radius: 8px;
                background: white;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                padding: 10px;
            }
            td {
                border: none;
                border-bottom: 1px solid #eee;
                position: relative;
                padding-left: 50%;
                text-align: right;
                min-height: 40px;
                display: flex;
                align-items: center;
                justify-content: flex-end;
            }
            td:last-child {
                border-bottom: 0;
            }
            td:before {
                content: attr(data-label); /* Use data-label for the new "header" */
                position: absolute;
                left: 10px;
                width: 45%;
                padding-right: 10px;
                white-space: nowrap;
                text-align: left;
                font-weight: bold;
                color: #333;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Blood Stock Expiring Soon (Next 7 Days)</h2>

        <%-- This assumes a servlet has set the 'expiring' attribute --%>
        <%-- Example: List<String[]> expiring = AlertsDAO.getExpiringStock(); request.setAttribute("expiring", expiring); --%>
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
                            <td data-label="Blood Group">${donation[0]}</td>
                            <td data-label="Units Expiring">${donation[1]}</td>
                            <td data-label="Expiry Date">${donation[2]}</td>
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