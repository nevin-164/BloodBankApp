package servlet;

import dao.DonationDAO;
import dao.AchievementDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Donation;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;

/**
 * ✅ FINAL VERSION: Handles the FINAL approval of a donation via a POST request.
 * This version is synchronized with the latest DAO methods and includes achievement processing.
 */
@WebServlet("/approve-donation")
public class ApproveDonationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            int donationId = Integer.parseInt(req.getParameter("donationId"));
            Date donationDate = Date.valueOf(req.getParameter("donationDate"));
            
            // First, get the user ID for the achievement processing later.
            Donation donation = DonationDAO.getDonationById(donationId);
            if (donation == null) {
                throw new Exception("Donation record not found.");
            }
            int userId = donation.getUserId();

            // ✅ CRITICAL FIX: Call the correct, final DAO method name.
            DonationDAO.approveAndProcessDonation(donationId, donationDate);
            
            // Process achievements for the donor
            processAchievements(userId);
            
            successMessage = "Donation #" + donationId + " has been successfully approved and completed.";

        } catch (Exception e) {
            errorMessage = "An error occurred during the approval process: " + e.getMessage();
            e.printStackTrace();
        }

        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }

    /**
     * Helper method to handle the achievement logic.
     */
    private void processAchievements(int userId) {
        try {
            int totalDonations = DonationDAO.getDonationCountForUser(userId);

            if (totalDonations >= 1 && !AchievementDAO.hasAchievement(userId, "First Donation")) {
                AchievementDAO.addAchievement(userId, "First Donation", "images/badges/first-donation.png");
            }
            if (totalDonations >= 5 && !AchievementDAO.hasAchievement(userId, "5-Time Donor")) {
                AchievementDAO.addAchievement(userId, "5-Time Donor", "images/badges/5-time.png");
            }
            if (totalDonations >= 10 && !AchievementDAO.hasAchievement(userId, "10-Time Donor")) {
                AchievementDAO.addAchievement(userId, "10-Time Donor", "images/badges/10-time.png");
            }
            if (!AchievementDAO.hasAchievement(userId, "Annual Donor")) {
                if (DonationDAO.getDonationCountInPastYear(userId) >= 2) {
                    AchievementDAO.addAchievement(userId, "Annual Donor", "images/badges/annual.png");
                }
            }
        } catch (Exception e) {
            // Log this error to the server console for debugging
            System.err.println("An error occurred during the achievement process: " + e.getMessage());
            e.printStackTrace();
        }
    }
}