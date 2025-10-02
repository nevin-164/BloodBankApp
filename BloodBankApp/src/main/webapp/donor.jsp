<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, dao.*, java.util.*, java.sql.Date, java.time.LocalDate, model.Achievement, model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
    
    // Set attributes for JSTL access
    request.setAttribute("user", u);
    request.setAttribute("isEligible", isEligible);
    request.setAttribute("appointment", appointment);
    request.setAttribute("emergencyExpiry", emergencyExpiry);
    request.setAttribute("hospitals", hospitals);
    request.setAttribute("achievements", AchievementDAO.getAchievementsForUser(u.getId()));
    request.setAttribute("myDonations", DonationDAO.getDonationsByUserId(u.getId()));
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
            --secondary-hover: #218838;
            --background-color: #f8f9fa;
            --panel-background: #ffffff;
            --text-color: #333;
            --text-light: #6c757d;
            --border-color: #e9ecef;
            --shadow: 0 6px 20px rgba(0,0,0,0.07);
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Poppins', sans-serif; background-color: var(--background-color); color: var(--text-color); }
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 20px; margin-bottom: 30px; border-bottom: 1px solid var(--border-color); }
        .header h1 { font-size: 2rem; font-weight: 700; color: var(--primary-color); }
        .header-nav a { color: var(--text-light); text-decoration: none; font-weight: 500; margin-left: 25px; transition: color 0.3s ease; }
        .header-nav a:hover { color: var(--primary-color); }
        .panel { background-color: var(--panel-background); padding: 35px; border-radius: 12px; box-shadow: var(--shadow); margin-bottom: 30px; }
        .panel-header { display: flex; align-items: center; margin-bottom: 25px; }
        .panel-header i { font-size: 1.5rem; margin-right: 15px; width: 40px; text-align: center; }
        .panel-header h3 { font-size: 1.5rem; font-weight: 600; color: var(--text-color); }
        .panel-header .fa-heartbeat { color: var(--secondary-color); }
        .panel-header .fa-star { color: #ffc107; }
        .panel-header .fa-first-aid { color: var(--primary-color); }
        .panel-header .fa-paper-plane { color: #007bff; }
        .panel-header .fa-history { color: #6c757d; }
        .form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; align-items: flex-end; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: 500; color: var(--text-light); }
        .form-group input, .form-group select { width: 100%; padding: 14px; border: 1px solid var(--border-color); border-radius: 8px; font-size: 1rem; font-family: 'Poppins', sans-serif; transition: border-color 0.2s, box-shadow 0.2s; }
        .form-group input:focus, .form-group select:focus { outline: none; border-color: var(--primary-color); box-shadow: 0 0 0 3px rgba(217, 83, 79, 0.1); }
        .btn { border: none; color: white; padding: 15px 25px; border-radius: 8px; cursor: pointer; font-family: 'Poppins', sans-serif; font-size: 1rem; font-weight: 600; transition: all 0.3s ease; width: 100%; }
        .btn-primary { background-color: var(--primary-color); }
        .btn-primary:hover { background-color: var(--primary-hover); transform: translateY(-2px); box-shadow: 0 4px 10px rgba(217, 83, 79, 0.2); }
        .btn-secondary { background-color: var(--secondary-color); }
        .btn-secondary:hover { background-color: var(--secondary-hover); transform: translateY(-2px); box-shadow: 0 4px 10px rgba(40, 167, 69, 0.2); }
        .status-box { padding: 20px; border-radius: 8px; border-left: 5px solid; margin-bottom: 25px; }
        .status-box strong { font-size: 1.1rem; }
        .status-box.eligible { background-color: #d4edda; border-color: var(--secondary-color); color: #155724; }
        .status-box.ineligible { background-color: #f8d7da; border-color: var(--primary-color); color: #721c24; }
        .appointment-info { background-color: #d1ecf1; border-left: 5px solid #0c5460; color: #0c5460; padding: 20px; border-radius: 8px; }
        .appointment-info p { margin: 5px 0; }
        .badge-list { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
        .badge { display: flex; align-items: center; background: #f8f9fa; border-radius: 8px; padding: 15px; border: 1px solid var(--border-color); }
        .badge img { width: 45px; height: 45px; margin-right: 15px; }
        .badge-info h4 { margin: 0; font-size: 1.1rem; color: #333; }
        .badge-info p { margin: 2px 0 0; font-size: 0.85rem; color: #777; }
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th, .data-table td { padding: 15px; text-align: left; border-bottom: 1px solid var(--border-color); }
        .data-table th { font-weight: 600; color: var(--text-light); font-size: 0.9rem; text-transform: uppercase; }
        .data-table tbody tr:last-child td { border-bottom: none; }
        .status-badge { padding: 6px 14px; border-radius: 20px; font-weight: 600; font-size: 0.8rem; color: white; text-transform: uppercase; display: inline-block; text-align: center; }
        .status-PENDING { background-color: #ffc107; color: #212529; }
        .status-PRE-SCREEN_PASSED { background-color: #007bff; }
        .status-APPROVED, .status-COMPLETED { background-color: var(--secondary-color); }
        .status-DECLINED, .status-CLOSED { background-color: var(--primary-color); }
        .status-fulfilled { background-color: #28a745; }
        .empty-state { text-align: center; padding: 40px; color: var(--text-light); font-style: italic; }
        #toast-container { position: fixed; top: 20px; right: 20px; z-index: 1000; }
        .toast { padding: 15px 25px; margin-bottom: 10px; border-radius: 8px; color: white; font-weight: 600; box-shadow: 0 5px 15px rgba(0,0,0,0.2); animation: slideIn 0.5s, fadeOut 0.5s 4.5s; }
        .toast.success { background-color: #28a745; }
        .toast.error { background-color: #dc3545; }
        @keyframes slideIn { from { transform: translateX(100%); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
        @keyframes fadeOut { from { opacity: 1; } to { opacity: 0; } }
    </style>
</head>
<body>

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
                    <i class="fas fa-heartbeat"></i>
                    <h3>Donation Status & Appointments</h3>
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
                            <strong>You are eligible to book a new donation appointment.</strong>
                        </div>
                        <form action="donate" method="post">
                            <div class="form-grid">
                                <div class="form-group">
                                    <label for="hospitalId">Choose a Hospital</label>
                                    <select id="hospitalId" name="hospitalId" required>
                                        <c:forEach var="h" items="${hospitals}">
                                            <option value="${h.id}">${h.name}</option>
                                        </c:forEach>
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
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button type="submit" class="btn btn-secondary">Request Appointment</button>
                                </div>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="status-box ineligible">
                            <strong>You are not yet eligible to book a new donation.</strong><br>
                            Your last donation was on: <fmt:formatDate value="${user.lastDonationDate}" pattern="MMM dd, yyyy"/><br>
                            Your next eligible date is: <strong><fmt:formatDate value="${user.nextEligibleDate}" pattern="MMM dd, yyyy"/></strong>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
            
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
                            <label for="unitsReq">Units Required</label>
                             <input type="number" id="unitsReq" name="units" min="1" required placeholder="e.g., 2">
                        </div>
                        <div class="form-group">
                           <label>&nbsp;</label> <button type="submit" class="btn btn-primary">Submit Request</button>
                        </div>
                    </div>
                </form>
            </section>

            <section class="panel">
                <div class="panel-header">
                     <i class="fas fa-first-aid"></i>
                     <h3>Emergency Donor Program</h3>
                </div>
                <c:choose>
                    <c:when test="${not empty emergencyExpiry}">
                         <div class="status-box eligible">
                            <p><strong>You are an active emergency donor.</strong> Thank you for your commitment!</p>
                            <p>Your availability expires on: <strong><fmt:formatDate value="${emergencyExpiry}" pattern="MMM dd, yyyy"/></strong></p>
                        </div>
                    </c:when>
                    <c:when test="${isEligible}">
                         <p>Help save lives in critical situations. Sign up to be available for emergency calls for one week.</p>
                         <form action="emergency-signup" method="post" style="margin-top:20px; max-width: 300px;">
                             <button type="submit" class="btn btn-primary">Sign Up as Emergency Donor</button>
                         </form>
                    </c:when>
                    <c:otherwise>
                        <p style="color: var(--text-light);">You are currently on a donation cooldown and cannot sign up as an emergency donor. This option will become available on your next eligible date.</p>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="panel">
                <div class="panel-header">
                    <i class="fas fa-star"></i>
                    <h3>Your Achievements</h3>
                </div>
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
                        <p class="empty-state">Your earned badges will appear here. Go donate to earn your first!</p>
                    </c:otherwise>
                </c:choose>
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
                        <c:if test="${empty myRequests}">
                            <tr><td colspan="5"><p class="empty-state">You have no pending or past requests.</p></td></tr>
                       </c:if>
                        <c:forEach var="req" items="${myRequests}">
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

            <section class="panel">
                <div class="panel-header">
                    <i class="fas fa-history"></i>
                    <h3>My Donation History</h3>
                </div>
                <c:choose>
                    <c:when test="${not empty myDonations}">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Appointment Date</th>
                                    <th>Hospital</th>
                                    <th>Units</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="don" items="${myDonations}">
                                    <tr>
                                        <td><fmt:formatDate value="${don.appointmentDate}" pattern="MMM dd, yyyy"/></td>
                                        <td><c:out value="${don.hospitalName != null ? don.hospitalName : 'N/A'}"/></td>
                                        
                                        <%-- ✅ DEFINITIVE FIX: Conditionally display units for the donor's history --%>
                                        <td>
                                            <c:choose>
                                                <c:when test="${don.units == 0}">
                                                    <span style="font-style: italic;">Emergency</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${don.units}"/>
                                                </c:otherwise>
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
            </section>
        </main>
    </div>

    <div id="toast-container"></div>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            function showToast(message, type) {
                if (!message || message.trim() === 'null' || message.trim() === '') return;
                const container = document.getElementById('toast-container');
                const toast = document.createElement('div');
                toast.className = `toast ${type}`;
                toast.textContent = decodeURIComponent(message.replace(/\+/g, ' '));
                container.appendChild(toast);
                setTimeout(() => toast.remove(), 5000);
            }
            showToast("<%= successMessage %>", 'success');
            showToast("<%= errorMessage %>", 'error');
        });
    </script>
</body>
</html>