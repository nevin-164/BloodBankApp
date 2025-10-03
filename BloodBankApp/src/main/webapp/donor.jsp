<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*, java.sql.Date, java.time.LocalDate, model.Achievement, model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    // Security check: Ensure a donor is logged in.
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser == null || !"DONOR".equals(sessionUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Always get the latest user data directly from the database to ensure accuracy.
    User u = UserDAO.getUserById(sessionUser.getId());
    
    // Update the session with this fresh data for consistency.
    session.setAttribute("user", u); 

    // --- Data Loading ---
    boolean isEligible = UserDAO.isDonorEligible(u.getId());
    Donation appointment = DonationDAO.getPendingAppointmentForDonor(u.getId());
    Date emergencyExpiry = EmergencyDonorDAO.getEmergencyStatusExpiry(u.getId());
    List<Hospital> hospitals = isEligible && appointment == null ? HospitalDAO.getAllHospitals() : new ArrayList<>();
    List<Donation> myDonations = DonationDAO.getDonationsByUserId(u.getId());
    
    // Set attributes for JSTL access
    request.setAttribute("user", u);
    request.setAttribute("isEligible", isEligible);
    request.setAttribute("appointment", appointment);
    request.setAttribute("emergencyExpiry", emergencyExpiry);
    request.setAttribute("hospitals", hospitals);
    request.setAttribute("achievements", AchievementDAO.getAchievementsForUser(u.getId()));
    request.setAttribute("myDonations", myDonations);
    request.setAttribute("myRequests", RequestDAO.getRequestsByUserId(u.getId()));
    
    // For toast notifications
    String successMessage = request.getParameter("success");
    String errorMessage = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>PLASMIC - Donor Dashboard</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
     <style>
        :root {
            --primary-color: #d9534f;
            --primary-hover: #c9302c;
            --secondary-color: #28a745;
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
        .donor-sidebar {
            width: 280px;
            background-color: var(--panel-background);
            padding: 30px;
            height: 100vh;
            position: sticky;
            top: 0;
            border-right: 1px solid var(--border-color);
        }
        .donor-sidebar .logo { font-size: 1.8rem; font-weight: 700; color: var(--primary-color); text-align: center; margin-bottom: 30px; }
        .donor-profile { text-align: center; }
        .donor-profile .blood-type { font-size: 2.5rem; font-weight: 700; color: var(--primary-color); background-color: #fde8e8; width: 80px; height: 80px; border-radius: 50%; display: grid; place-items: center; margin: 0 auto 15px auto; }
        .donor-profile h3 { font-size: 1.3rem; margin-bottom: 5px; }
        .donor-profile p { color: var(--text-light); margin-bottom: 20px; }
        .donor-stats { text-align: left; margin-bottom: 30px; }
        .stat-item { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; }
        .stat-item i { color: var(--primary-color); font-size: 1.2rem; }
        .stat-item span { font-weight: 600; }
        .sidebar-nav { list-style: none; }
        .sidebar-nav li a { display: block; padding: 12px 0; text-decoration: none; color: var(--text-light); font-weight: 500; transition: color 0.3s; }
        .sidebar-nav li a:hover { color: var(--primary-color); }
        
        /* Main Content Styles */
        .main-content { flex-grow: 1; padding: 30px; }
        .main-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
        .main-header h1 { font-size: 2rem; font-weight: 700; }
        .main-header .header-nav a { color: var(--text-light); text-decoration: none; font-weight: 500; margin-left: 25px; transition: color 0.3s ease; }
        .main-header .header-nav a:hover { color: var(--primary-color); }
        
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
        .btn-secondary { background-color: var(--secondary-color); }
        .btn-secondary:hover { background-color: var(--secondary-hover); transform: translateY(-2px); box-shadow: 0 4px 10px rgba(40, 167, 69, 0.2); }
        
        /* Status & Info Boxes */
        .status-box { padding: 20px; border-radius: 8px; border-left: 5px solid; margin-bottom: 25px; display: flex; align-items: center; gap: 15px; }
        .status-box i { font-size: 1.5rem; }
        .status-box.eligible { background-color: #d4edda; border-color: var(--secondary-color); color: #155724; }
        .status-box.ineligible { background-color: #f8d7da; border-color: var(--primary-color); color: #721c24; }
        .appointment-info { background-color: #d1ecf1; border-left: 5px solid #0c5460; color: #0c5460; padding: 20px; border-radius: 8px; }
        
        /* Tabs and Tables */
        .tabs { display: flex; border-bottom: 2px solid var(--border-color); margin-bottom: 20px; }
        .tab-link { padding: 10px 20px; cursor: pointer; font-weight: 600; color: var(--text-light); border-bottom: 3px solid transparent; }
        .tab-link.active { color: var(--primary-color); border-bottom-color: var(--primary-color); }
        .tab-content { display: none; }
        .tab-content.active { display: block; }
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th, .data-table td { padding: 15px; text-align: left; border-bottom: 1px solid var(--border-color); }
        .data-table th { font-weight: 600; color: var(--text-light); font-size: 0.9rem; text-transform: uppercase; }
        .status-badge { padding: 5px 12px; border-radius: 20px; font-weight: 600; font-size: 0.8rem; color: white; text-transform: uppercase; }
        
        /* ✅ CORRECTED AND EXPANDED CSS STATUS RULES */
        /* ✅ DEFINITIVE CSS FIX FOR ALL STATUS BADGES */
/* Yellow for Pending */
.status-PENDING { 
    background-color: #ffc107; 
    color: #212529; 
}
/* Blue for In-Progress */
.status-PRE-SCREEN_PASSED { 
    background-color: #007bff; 
}
/* Green for Success */
.status-COMPLETED, .status-APPROVED, .status-FULFILLED {
    background-color: var(--secondary-color);
}
/* Red for Negative */
.status-DECLINED, .status-CLOSED, .status-CANCELLED {
    background-color: var(--primary-color);
}
        .badge-list { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
        .badge { display: flex; align-items: center; background: #f8f9fa; border-radius: 8px; padding: 15px; border: 1px solid var(--border-color); }
        .badge img { width: 45px; height: 45px; margin-right: 15px; }
        .empty-state { text-align: center; padding: 40px; color: var(--text-light); font-style: italic; }
    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />

<div class="dashboard-container">
    <aside class="donor-sidebar">
        <div class="logo">PLASMIC</div>
        <div class="donor-profile">
            <div class="blood-type">${user.bloodGroup}</div>
            <h3><c:out value="${user.name}"/></h3>
            <p><c:out value="${user.email}"/></p>
        </div>
        <div class="donor-stats">
            <div class="stat-item"><i class="fas fa-tint"></i> <span>Total Donations: ${fn:length(myDonations)}</span></div>
            <div class="stat-item"><i class="fas fa-life-ring"></i> <span>Lives Saved: ${fn:length(myDonations) * 3}</span></div>
        </div>
        <ul class="sidebar-nav">
            <li><a href="${pageContext.request.contextPath}/public-dashboard">Public View</a></li>
            <li><a href="${pageContext.request.contextPath}/community">Community Forum</a></li>
            <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
        </ul>
    </aside>

    <main class="main-content">
        <header class="main-header">
            <h1>Welcome, <c:out value="${user.name}"/>!</h1>
        </header>

        <section class="panel">
            <div class="panel-header">
                <i class="fas fa-heartbeat"></i><h3>Donation Status & Appointments</h3>
            </div>
            <c:choose>
                <c:when test="${not empty appointment}">
                    <div class="appointment-info">
                        <h3>Your Upcoming Appointment</h3>
                        <p><strong>Date:</strong> <fmt:formatDate value="${appointment.appointmentDate}" pattern="MMMM dd, yyyy"/></p>
                        <p><strong>Hospital:</strong> <c:out value="${appointment.hospitalName}"/></p>
                        <p><strong>Status:</strong> <span class="status-badge status-${appointment.status}"><c:out value="${appointment.status.replace('_', ' ')}"/></span></p>
                    </div>
                </c:when>
                <c:when test="${isEligible}">
                    <div class="status-box eligible">
                        <i class="fas fa-check-circle"></i>
                        <div><strong>You are eligible to book a new donation appointment.</strong></div>
                    </div>
                    <form action="donate" method="post">
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="hospitalId">Choose a Hospital</label>
                                <select id="hospitalId" name="hospitalId" required>
                                    <c:forEach var="h" items="${hospitals}"><option value="${h.id}">${h.name}</option></c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="appointmentDate">Preferred Date</label>
                                <input type="date" id="appointmentDate" name="appointmentDate" required min="<%= LocalDate.now().plusDays(1) %>" max="<%= LocalDate.now().plusDays(30) %>">
                            </div>
                            <div class="form-group">
                                <label for="units">Units</label>
                                <input type="number" id="units" name="units" min="1" value="1" required>
                            </div>
                            <div><button type="submit" class="btn btn-secondary">Request Appointment</button></div>
                        </div>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="status-box ineligible">
                        <i class="fas fa-times-circle"></i>
                        <div>
                            <strong>You are not yet eligible to book a new donation.</strong><br>
                            Your last donation was on <fmt:formatDate value="${user.lastDonationDate}" pattern="MMM dd, yyyy"/>, and your next eligible date is <strong><fmt:formatDate value="${user.nextEligibleDate}" pattern="MMM dd, yyyy"/></strong>.
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="panel">
            <div class="panel-header"><i class="fas fa-first-aid"></i><h3>Emergency Donor Program</h3></div>
            <c:choose>
                <c:when test="${not empty emergencyExpiry}">
                     <div class="status-box eligible">
                        <i class="fas fa-shield-alt"></i>
                        <div><strong>You are an active emergency donor.</strong> Thank you! Your availability expires on <strong><fmt:formatDate value="${emergencyExpiry}" pattern="MMM dd, yyyy"/></strong>.</div>
                    </div>
                </c:when>
                <c:when test="${isEligible}">
                     <p>Help save lives in critical situations. Sign up to be available for emergency calls for one week.</p>
                     <form action="emergency-signup" method="post" style="margin-top:20px; max-width: 300px;">
                         <button type="submit" class="btn btn-primary">Sign Up as Emergency Donor</button>
                     </form>
                </c:when>
                <c:otherwise>
                    <p style="color: var(--text-light);">You are on a donation cooldown and cannot sign up as an emergency donor. This will be available on your next eligible date.</p>
                </c:otherwise>
            </c:choose>
        </section>

        <div class="tabs">
            <div class="tab-link active" onclick="openTab(event, 'achievements')">Achievements</div>
            <div class="tab-link" onclick="openTab(event, 'requests')">My Requests</div>
            <div class="tab-link" onclick="openTab(event, 'donations')">Donation History</div>
        </div>

        <div id="achievements" class="tab-content active panel">
            <c:choose>
                <c:when test="${not empty achievements}">
                    <div class="badge-list">
                        <c:forEach var="ach" items="${achievements}">
                            <div class="badge">
                                <img src="${pageContext.request.contextPath}/${ach.badgeIcon}" alt="${ach.badgeName}">
                                <div class="badge-info">
                                    <h4>${ach.badgeName}</h4>
                                    <p>Earned on: <fmt:formatDate value="${ach.dateEarned}" pattern="MMM dd, yyyy"/></p>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="empty-state">Your earned badges will appear here. Donate to earn your first!</p>
                </c:otherwise>
            </c:choose>
        </div>

        <div id="requests" class="tab-content panel">
             <form action="request-blood" method="post" style="margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid var(--border-color);">
                <div class="form-grid">
                    <div class="form-group"><label for="bloodGroup">Blood Group Needed</label><select id="bloodGroup" name="bloodGroup" required><option>A+</option><option>A-</option><option>B+</option><option>B-</option><option>AB+</option><option>AB-</option><option>O+</option><option>O-</option></select></div>
                    <div class="form-group"><label for="unitsReq">Units Required</label><input type="number" id="unitsReq" name="units" min="1" required placeholder="e.g., 2"></div>
                    <div><button type="submit" class="btn btn-primary">Submit Request</button></div>
                </div>
            </form>
            <table class="data-table">
                 <thead><tr><th>Date</th><th>Blood Group</th><th>Units</th><th>Status</th><th>Fulfilled By</th></tr></thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty myRequests}">
                            <tr><td colspan="5"><p class="empty-state">You have no pending or past requests.</p></td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="req" items="${myRequests}">
                                <tr>
                                     <td><fmt:formatDate value="${req.createdAt}" pattern="MMM dd, yyyy"/></td>
                                     <td>${req.bloodGroup}</td>
                                     <td>${req.units}</td>
                                    <%-- ✅ THIS IS THE CORRECTED LINE --%>
<td><span class="status-badge status-${req.status}">${req.status}</span></td>
                                     <td>${req.hospitalName != null ? req.hospitalName : "N/A"}</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <div id="donations" class="tab-content panel">
             <c:choose>
                <c:when test="${not empty myDonations}">
                    <table class="data-table">
                        <thead><tr><th>Appointment Date</th><th>Hospital</th><th>Units</th><th>Status</th></tr></thead>
                        <tbody>
                            <c:forEach var="don" items="${myDonations}">
                                <tr>
                                    <td><fmt:formatDate value="${don.appointmentDate}" pattern="MMM dd, yyyy"/></td>
                                    <td><c:out value="${don.hospitalName != null ? don.hospitalName : 'N/A'}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${don.units == 0}"><span style="font-style: italic;">Emergency</span></c:when>
                                            <c:otherwise><c:out value="${don.units}"/></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><span class="status-badge status-${don.status}">${don.status.replace("_", " ")}</span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p class="empty-state">You have no past donation appointments.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<script>
    function openTab(evt, tabName) {
        var i, tabcontent, tablinks;
        tabcontent = document.getElementsByClassName("tab-content");
        for (i = 0; i < tabcontent.length; i++) {
            tabcontent[i].style.display = "none";
        }
        tablinks = document.getElementsByClassName("tab-link");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }
        document.getElementById(tabName).style.display = "block";
        evt.currentTarget.className += " active";
    }
</script>

</body>
</html>