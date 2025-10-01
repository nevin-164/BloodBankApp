package servlet;

import dao.DonationDAO;
import dao.AchievementDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;

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
            // ✅ Donation ID
            String donationIdStr = req.getParameter("donationId");
            if (donationIdStr == null || donationIdStr.isEmpty()) {
                throw new IllegalArgumentException("Donation ID is missing.");
            }
            int donationId = Integer.parseInt(donationIdStr);

            // ✅ Donation Date (safe parsing)
            String dateStr = req.getParameter("donationDate");
            if (dateStr == null || dateStr.isEmpty()) {
                throw new IllegalArgumentException("Donation date is missing.");
            }

            Date donationDate;
            try {
                donationDate = Date.valueOf(dateStr); // expects yyyy-MM-dd
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd.");
            }

            // ✅ Approve donation transaction
            int userId = DonationDAO.getDonationById(donationId).getUserId();
            DonationDAO.approveDonationTransaction(donationId, donationDate);

            // ✅ Award achievements
            processAchievements(userId);

            successMessage = "Donation #" + donationId + " has been successfully approved and completed.";

        } catch (Exception e) {
            errorMessage = "An error occurred during the approval process: " + e.getMessage();
            e.printStackTrace();
        }

        // ✅ Redirect with proper message
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
            System.err.println("An error occurred during the achievement process: " + e.getMessage());
        }
    }
}
