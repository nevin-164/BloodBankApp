package servlet;

import dao.RequestDAO;
import dao.UserDAO; 
import dao.AchievementDAO; // ✅ ADDED: Import our new DAO
import model.Hospital; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder; 
import java.sql.Date; 
import java.time.LocalDate; 

@WebServlet("/fulfill-via-emergency")
public class FulfillViaEmergencyServlet extends HttpServlet {
    private static final int COOLING_DAYS = 90; // Standard 90-day waiting period

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;
        
        if (hospital == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            int requestId = Integer.parseInt(req.getParameter("requestId"));
            int donorId = Integer.parseInt(req.getParameter("donorId"));

            // 1. Mark the patient's request as FULFILLED
            RequestDAO.updateRequestStatus(requestId, "FULFILLED");

            // 2. Update the emergency donor's eligibility dates
            LocalDate today = LocalDate.now();
            Date lastDonationDate = Date.valueOf(today);
            Date nextEligibleDate = Date.valueOf(today.plusDays(COOLING_DAYS));
            UserDAO.updateDonationDates(donorId, lastDonationDate, nextEligibleDate);
            
            // 3. Log this action for analytics
            RequestDAO.logRequestAction(requestId, hospital.getId(), "FULFILLED");
            
            // --- ✅ NEW: Gamification Logic ---
            try {
                // Award the "Emergency Hero" badge
                boolean hasBadge = AchievementDAO.hasAchievement(donorId, "Emergency Hero");
                if (!hasBadge) {
                    AchievementDAO.addAchievement(donorId, 
                                                  "Emergency Hero", 
                                                  "images/badges/emergency-hero.png");
                }
            } catch (Exception e_ach) {
                // If gamification fails, don't stop the whole process.
                System.err.println("Gamification (Emergency Hero) Error: " + e_ach.getMessage());
            }
            // --- End Gamification Logic ---
            
            successMessage = "Request " + requestId + " fulfilled via emergency donor!";
            
        } catch (Exception e) {
            errorMessage = "Error fulfilling request via emergency donor";
            e.printStackTrace();
        }
        
        // Redirect to the SERVLET, not the JSP.
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
}