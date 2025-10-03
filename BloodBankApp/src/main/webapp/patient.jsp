<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User, model.Request, dao.RequestDAO, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    // Security check and data retrieval
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Request> requests = RequestDAO.getRequestsByUserId(user.getId());
    pageContext.setAttribute("requests", requests);
    
    String successMessage = request.getParameter("success");
    String errorMessage = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>PLASMIC - Patient Dashboard</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        :root {
            --primary-color: #d9534f;
            --primary-hover: #c9302c;
            --background-color: #f8f9fa;
            --panel-background: #ffffff;
            --text-color: #333;
            --text-light: #6c757d;
            --border-color: #e9ecef;
            --shadow: 0 6px 20px rgba(0,0,0,0.07);
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--background-color);
            color: var(--text-color);
        }

        .container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px 0;
            margin-bottom: 30px;
            border-bottom: 1px solid var(--border-color);
        }

        .header h1 {
            font-size: 2rem;
            font-weight: 700;
            color: var(--primary-color);
        }
        
        .header-nav a {
            color: var(--text-light);
            text-decoration: none;
            font-weight: 500;
            margin-left: 25px;
            transition: color 0.3s ease;
        }

        .header-nav a:hover {
            color: var(--primary-color);
        }

        .panel {
            background-color: var(--panel-background);
            padding: 35px;
            border-radius: 12px;
            box-shadow: var(--shadow);
            margin-bottom: 30px;
        }

        .panel-header {
            display: flex;
            align-items: center;
            margin-bottom: 25px;
        }

        .panel-header i {
            font-size: 1.5rem;
            color: var(--primary-color);
            margin-right: 15px;
            width: 40px;
            text-align: center;
        }

        .panel-header h3 {
            font-size: 1.5rem;
            font-weight: 600;
            color: var(--text-color);
        }
        
        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            align-items: flex-end;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: var(--text-light);
        }

        .form-group input, .form-group select {
            width: 100%;
            padding: 14px;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            font-size: 1rem;
            font-family: 'Poppins', sans-serif;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        
        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(217, 83, 79, 0.1);
        }

        .btn {
            border: none;
            color: white;
            padding: 15px 25px;
            border-radius: 8px;
            cursor: pointer;
            font-family: 'Poppins', sans-serif;
            font-size: 1rem;
            font-weight: 600;
            transition: all 0.3s ease;
            width: 100%;
        }

        .btn-primary {
            background-color: var(--primary-color);
        }
        
        .btn-primary:hover {
            background-color: var(--primary-hover);
            transform: translateY(-2px);
            box-shadow: 0 4px 10px rgba(217, 83, 79, 0.2);
        }

        .request-table {
            width: 100%;
            border-collapse: collapse;
        }

        .request-table th, .request-table td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid var(--border-color);
        }

        .request-table th {
            font-weight: 600;
            color: var(--text-light);
            font-size: 0.9rem;
            text-transform: uppercase;
        }
        
        .request-table tbody tr:last-child td {
            border-bottom: none;
        }

        .status-badge {
            padding: 6px 14px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 0.8rem;
            color: white;
            text-transform: uppercase;
            display: inline-block;
            text-align: center;
        }

        .status-pending { background-color: #ffc107; color: #212529; }
        .status-fulfilled { background-color: #28a745; }
        .status-declined { background-color: #dc3545; }
        
        .empty-state {
            text-align: center;
            padding: 40px;
            color: var(--text-light);
            font-style: italic;
        }

    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />
    <div class="container">
        <header class="header">
            <h1>Welcome, <c:out value="${user.name}"/>!</h1>
            <nav class="header-nav">
                <a href="${pageContext.request.contextPath}/public-dashboard">Public View</a>
                <a href="${pageContext.request.contextPath}/community">Community Forum</a>
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </nav>
        </header>

        <main>
            <section class="panel">
                <div class="panel-header">
                    <i class="fas fa-paper-plane"></i>
                    <h3>Submit a Blood Request</h3>
                </div>
                <form action="request-blood" method="post">
                    <div class="form-grid">
                        <div class="form-group">
                            <label for="bloodGroup">Blood Group Needed</label>
                            <select id="bloodGroup" name="bloodGroup" required>
                                <option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="units">Units Required</label>
                            <input type="number" id="units" name="units" min="1" required placeholder="e.g., 2">
                        </div>
                        <div class="form-group">
                             <label>&nbsp;</label> <button type="submit" class="btn btn-primary">Submit Request</button>
                        </div>
                    </div>
                </form>
            </section>

            <section class="panel">
                <div class="panel-header">
                    <i class="fas fa-history"></i>
                    <h3>Your Request History</h3>
                </div>
                <table class="request-table">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Blood Group</th>
                            <th>Units</th>
                            <th>Status</th>
                            <th>Fulfilled By</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty requests}">
                            <tr><td colspan="5"><p class="empty-state">You have no pending or past requests.</p></td></tr>
                        </c:if>
                        <c:forEach var="req" items="${requests}">
                            <tr>
                                <td><fmt:formatDate value="${req.createdAt}" pattern="MMM dd, yyyy"/></td>
                                <td>${req.bloodGroup}</td>
                                <td>${req.units}</td>
                                <td>
                                    <span class="status-badge status-${req.status.toLowerCase()}">
                                        ${req.status}
                                    </span>
                                </td>
                                <td>${req.hospitalName != null ? req.hospitalName : "N/A"}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </section>
        </main>
    </div>


  
</body>
</html>