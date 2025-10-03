<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User adminUser = (User) session.getAttribute("user");
    // Extra security check
    if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - User Profile: ${profileUser.name}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --brand-red: #c9302c;
            --brand-blue: #007bff;
            --light-gray: #f8f9fa;
            --medium-gray: #e9ecef;
            --text-color: #333;
        }
        * { box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--light-gray);
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .header h1 {
            color: var(--brand-red);
            margin: 0;
        }
        .header a {
            text-decoration: none;
            color: var(--brand-red);
            font-weight: 600;
        }
        
        /* Profile Header */
        .profile-header {
            background: #fff;
            padding: 25px 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            margin-bottom: 20px;
        }
        .profile-header h2 {
            margin: 0 0 10px 0;
            font-size: 1.8rem;
            color: var(--brand-red);
        }
        .profile-details p {
            margin: 5px 0;
            font-size: 1.1rem;
            color: #555;
        }
        .profile-details strong {
            color: var(--text-color);
        }

        /* Grid for History */
        .history-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        /* Badge & History Card Styles */
        .history-card, .achievements-card {
            background: #fff;
            padding: 20px 25px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        .history-card h3, .achievements-card h3 {
            margin-top: 0;
            border-bottom: 1px solid var(--medium-gray);
            padding-bottom: 10px;
            color: #333;
        }
        
        /* Tables for History */
        .history-table {
            width: 100%;
            border-collapse: collapse;
        }
        .history-table th, .history-table td {
            padding: 8px 0;
            text-align: left;
            border-bottom: 1px solid var(--medium-gray);
            font-size: 0.9rem;
        }
        .history-table th {
            font-weight: 600;
        }
        .history-table tr:last-child td {
            border-bottom: none;
        }

        /* Badge List Styles */
        .badge-list {
            list-style: none;
            padding: 0;
        }
        .badge {
            display: flex;
            align-items: center;
            background: var(--light-gray);
            border-radius: 8px;
            padding: 10px;
            margin-bottom: 10px;
        }
        .badge img {
            width: 40px;
            height: 40px;
            margin-right: 10px;
        }
        .badge-info h4 {
            margin: 0;
            font-size: 1rem;
            color: var(--text-color);
        }
        .badge-info p {
            margin: 0;
            font-size: 0.8rem;
            color: #6c757d;
        }

        @media (max-width: 768px) {
            .history-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Donor Profile</h1>
            <a href="${pageContext.request.contextPath}/admin/donor-list">← Back to Donor List</a>
        </div>

        <div class="profile-header">
            <h2>${profileUser.name}</h2>
            <div class="profile-details">
                <p><strong>User ID:</strong> ${profileUser.id}</p>
                <p><strong>Email:</strong> ${profileUser.email}</p>
                <p><strong>Role:</strong> ${profileUser.role}</p>
                <p><strong>Blood Group:</strong> ${profileUser.bloodGroup}</p>
            </div>
        </div>
        
        <div class="achievements-card" style="margin-bottom: 20px;">
            <h3>Achievements</h3>
            <c:if test="${empty achievements}">
                <p>No achievements earned yet.</p>
            </c:if>
            <ul class="badge-list">
                <c:forEach var="ach" items="${achievements}">
                    <li class="badge">
                        <img src="${pageContext.request.contextPath}/${ach.badgeIcon}" alt="${ach.badgeName}">
                        <div class="badge-info">
                            <h4>${ach.badgeName}</h4>
                            <p>Earned on: ${ach.dateEarned}</p>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </div>
        
        <div class="history-grid">
            <div class="history-card">
                <h3>Donation History</h3>
                <c:if test="${empty donationHistory}">
                    <p>This user has no donation history.</p>
                </c:if>
                <c:if test="${not empty donationHistory}">
                    <table class="history-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Hospital</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${donationHistory}">
                                <tr>
                                    <td>${d.appointmentDate}</td>
                                    <td>${d.hospitalName}</td>
                                    <td>${d.status}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>

            <div class="history-card">
                <h3>Blood Request History</h3>
                <c:if test="${empty requestHistory}">
                    <p>This user has no request history.</p>
                </c:if>
                <c:if test="${not empty requestHistory}">
                    <table class="history-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Blood Group</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${requestHistory}">
                                <tr>
                                    <td>${r.createdAt}</td>
                                    <td>${r.bloodGroup}</td>
                                    <%-- ✅ THE FIX: Conditionally display the status message --%>
                                    <td>
                                        <c:choose>
                                            <c:when test="${r.status == 'FULFILLED'}">
                                                req fullfiled by ${r.hospitalName}
                                            </c:when>
                                            <c:otherwise>
                                                ${r.trackingStatus}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>