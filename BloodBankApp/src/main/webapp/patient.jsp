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
    <title>PLASMIC - Patient Dashboard</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        :root {
            --primary-color: #d9534f;
            --primary-hover: #c9302c;
            --background-color: #f4f7fa;
            --panel-background: #ffffff;
            --text-color: #333;
            --text-light: #6c757d;
            --border-color: #e9ecef;
            --shadow: 0 8px 25px rgba(0,0,0,0.07);
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Poppins', sans-serif; background-color: var(--background-color); color: var(--text-color); }
        .dashboard-container { display: flex; align-items: flex-start; }
        
        /* Sidebar Styles */
        .patient-sidebar {
            width: 280px;
            background-color: var(--panel-background);
            padding: 30px;
            height: 100vh;
            position: sticky;
            top: 0;
            border-right: 1px solid var(--border-color);
        }
        .patient-sidebar .logo { font-size: 1.8rem; font-weight: 700; color: var(--primary-color); text-align: center; margin-bottom: 30px; }
        .patient-profile { text-align: center; }
        .patient-profile .icon { font-size: 2.5rem; color: var(--primary-color); background-color: #fde8e8; width: 80px; height: 80px; border-radius: 50%; display: grid; place-items: center; margin: 0 auto 15px auto; }
        .patient-profile h3 { font-size: 1.3rem; margin-bottom: 5px; }
        .patient-profile p { color: var(--text-light); margin-bottom: 20px; }
        .sidebar-nav { list-style: none; margin-top: 30px; }
        .sidebar-nav li a { display: block; padding: 12px 0; text-decoration: none; color: var(--text-light); font-weight: 500; transition: color 0.3s; }
        .sidebar-nav li a:hover { color: var(--primary-color); }

        /* Main Content Styles */
        .main-content { flex-grow: 1; padding: 30px; }
        .main-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
        .main-header h1 { font-size: 2rem; font-weight: 700; }
        
        /* Panel & Form Styles */
        .panel { background-color: var(--panel-background); padding: 30px; border-radius: 12px; box-shadow: var(--shadow); margin-bottom: 30px; }
        .panel-header { display: flex; align-items: center; gap: 15px; margin-bottom: 25px; }
        .panel-header i { font-size: 1.2rem; color: var(--primary-color); }
        .panel-header h3 { font-size: 1.3rem; font-weight: 600; color: var(--text-color); }
        .form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; align-items: flex-end; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: 500; color: var(--text-light); font-size: 0.9rem;}
        .form-group input, .form-group select { width: 100%; padding: 12px; border: 1px solid var(--border-color); border-radius: 8px; font-size: 1rem; font-family: 'Poppins', sans-serif; transition: border-color 0.2s, box-shadow 0.2s; }
        .form-group input:focus, .form-group select:focus { outline: none; border-color: var(--primary-color); box-shadow: 0 0 0 3px rgba(217, 83, 79, 0.1); }
        .btn { border: none; color: white; padding: 14px 25px; border-radius: 8px; cursor: pointer; font-family: 'Poppins', sans-serif; font-size: 1rem; font-weight: 600; transition: all 0.3s ease; width: 100%; }
        .btn-primary { background-color: var(--primary-color); }
        .btn-primary:hover { background-color: var(--primary-hover); transform: translateY(-2px); box-shadow: 0 4px 10px rgba(217, 83, 79, 0.2); }
        
        /* Table Styles */
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th, .data-table td { padding: 15px; text-align: left; border-bottom: 1px solid var(--border-color); }
        .data-table th { font-weight: 600; color: var(--text-light); font-size: 0.9rem; text-transform: uppercase; }
        .status-badge { padding: 5px 12px; border-radius: 20px; font-weight: 600; font-size: 0.8rem; color: white; text-transform: uppercase; }
        
        .status-pending { background-color: #ffc107; color: #212529; }
        .status-approved, .status-fulfilled { background-color: #28a745; }
        .status-declined, .status-closed { background-color: #dc3545; }
        
        .empty-state { text-align: center; padding: 40px; color: var(--text-light); font-style: italic; }
    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />

<div class="dashboard-container">
    <aside class="patient-sidebar">
        <div class="logo">PLASMIC</div>
        <div class="patient-profile">
            <div class="icon"><i class="fas fa-user-injured"></i></div>
            <h3><c:out value="${user.name}"/></h3>
            <p><c:out value="${user.email}"/></p>
        </div>
        <ul class="sidebar-nav">
            <li><a href="${pageContext.request.contextPath}/public-dashboard">Public View</a></li>
            <li><a href="${pageContext.request.contextPath}/community">Community Forum</a></li>
            <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
        </ul>
    </aside>

    <main class="main-content">
        <header class="main-header">
            <h1>Patient Dashboard</h1>
        </header>

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
                    <div>
                        <button type="submit" class="btn btn-primary">Submit Request</button>
                    </div>
                </div>
            </form>
        </section>

        <section class="panel">
            <div class="panel-header">
                <i class="fas fa-history"></i>
                <h3>Your Request History</h3>
            </div>
            <table class="data-table">
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