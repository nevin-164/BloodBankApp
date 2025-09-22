package servlet;

import dao.DonationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/update-donation-status")
public class UpdateDonationStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String successMessage = "";
        String errorMessage = "";
        
        try {
            int donationId = Integer.parseInt(req.getParameter("donationId"));
            String newStatus = req.getParameter("status");

            if ("PRE-SCREEN_PASSED".equals(newStatus)) {
                DonationDAO.updateDonationStatus(donationId, "PRE-SCREEN_PASSED");
                successMessage = "Donor " + donationId + " has passed pre-screening. Ready for donation.";
            } else if ("CANCELLED".equals(newStatus)) {
                DonationDAO.updateDonationStatus(donationId, "CANCELLED");
                successMessage = "Appointment " + donationId + " has been cancelled.";
            } else {
                errorMessage = "Invalid status update.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Error updating donation status.";
        }
        
        // Redirect back to the hospital dashboard to show the changes
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
}