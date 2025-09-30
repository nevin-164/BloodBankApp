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

@WebServlet("/update-donation-status")
public class UpdateDonationStatusServlet extends HttpServlet {
    
    /**
     * ✅ FINAL VERSION: Handles status updates for donations by a hospital.
     * This servlet is typically used for intermediate steps, like moving a donation
     * from 'PENDING' to 'PRE-SCREEN_PASSED'. It has been updated to correctly use the
     * doPost method for data modification, fixing the "405 Method Not Allowed" error.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        // --- 1. Security Check ---
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        // --- 2. Process the Status Update ---
        try {
            int donationId = Integer.parseInt(req.getParameter("donationId"));
            String newStatus = req.getParameter("newStatus");

            // Input validation: ensure a valid status is provided.
            if (newStatus == null || newStatus.trim().isEmpty()) {
                errorMessage = "No new status was provided for the update.";
            } else {
                // Calls the correct, dedicated public method in the DAO.
                DonationDAO.updateDonationStatusForHospital(donationId, newStatus);
                successMessage = "Donation #" + donationId + " status has been updated to '" + newStatus + "'.";
            }

        } catch (NumberFormatException e) {
            errorMessage = "Invalid donation ID format.";
        } catch (Exception e) {
            errorMessage = "An error occurred while updating the donation status.";
            e.printStackTrace(); // Log the full error for debugging.
        }

        // --- 3. Redirect Back to the Dashboard ---
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
    
    /**
     * Handles any accidental GET requests by simply redirecting to the dashboard.
     * This prevents errors if a user bookmarks or directly accesses the URL.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect(req.getContextPath() + "/hospital-dashboard");
    }
}

