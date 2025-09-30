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
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/approve-donation")
public class ApproveDonationServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Processes donation approvals using a database transaction.
     * This servlet's logic is now much simpler and more robust. It calls a single,
     * transaction-safe method in the DAO to handle all database updates,
     * preventing the data inconsistency issues previously observed.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            int donationId = Integer.parseInt(req.getParameter("donationId"));
            Date dateDonated = Date.valueOf(LocalDate.now());

            // ✅ FIXED: Call the single, transaction-safe method to approve the donation.
            // This one method handles everything: updating dates, adding bags, setting status, and donor eligibility.
            DonationDAO.approveDonationTransaction(donationId, dateDonated);
            
            // Gamification can be processed separately after the main transaction is successful.
            // We need to get the user ID for the achievement check.
            int userId = DonationDAO.getDonationById(donationId).getUserId();
            processAchievements(userId);
            
            successMessage = "Donation approved! Inventory and donor eligibility have been updated successfully.";

        } catch (NumberFormatException e) {
            errorMessage = "Invalid Donation ID provided.";
        } catch (SQLException e) {
            // This will now catch errors from the transaction, like if a step failed and was rolled back.
            errorMessage = "A database error occurred during approval. The operation was safely cancelled. Details: " + e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage = "A general error occurred while trying to approve the donation.";
            e.printStackTrace();
        }

        // --- Redirect back to the hospital dashboard ---
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        res.sendRedirect(redirectURL);
    }

    /**
     * Helper method to handle the achievement logic, called after the main transaction succeeds.
     * @param userId The ID of the donor who just donated.
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
            // Log the error but don't stop the main process. Gamification is secondary.
            System.err.println("An error occurred during the achievement process: " + e.getMessage());
        }
    }
}