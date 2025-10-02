package servlet;

import dao.DonationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;

/**
 * ✅ FINAL VERSION: Handles the FINAL approval of a donation via a POST request.
 * This servlet now calls the correct transactional method in the DAO, which
 * handles donation approval, inventory creation, and achievement awards all at once.
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
            
            // ✅ CRITICAL FIX: Call the correct, centralized, and transaction-safe DAO method.
            // This single method now handles everything: updating status, creating bags,
            // setting the donor cooldown, and awarding achievements.
            DonationDAO.approveDonationTransaction(donationId, donationDate);
            
            successMessage = "Donation #" + donationId + " has been successfully approved. The blood bags are now pending lab tests.";

        } catch (NumberFormatException e) {
            errorMessage = "Invalid ID or date format provided.";
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage = "An error occurred during the approval process: " + e.getMessage();
            e.printStackTrace();
        }

        // Redirect back to the hospital dashboard with a status message.
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
    
    // The separate processAchievements helper method has been removed as this
    // logic is now correctly handled inside DonationDAO.approveDonationTransaction
    // to ensure it's part of the same database transaction.
}