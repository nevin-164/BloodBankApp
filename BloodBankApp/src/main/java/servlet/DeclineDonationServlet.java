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

@WebServlet("/decline-donation")
public class DeclineDonationServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Handles the declining of a pending donation appointment by a hospital.
     * This servlet has been updated to call the new, dedicated public method 
     * in the DAO, resolving the previous compile error and adhering to safer design practices.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // --- 1. Security Check ---
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        // --- 2. Process the Decline Action ---
        try {
            // Get the ID of the donation to be declined from the URL parameter.
            int donationId = Integer.parseInt(req.getParameter("donationId"));

            // ✅ FIXED: Call the new, dedicated public method for declining.
            DonationDAO.declineDonation(donationId);

            successMessage = "Donation appointment #" + donationId + " has been successfully declined.";

        } catch (NumberFormatException e) {
            errorMessage = "Invalid donation ID format.";
        } catch (Exception e) {
            errorMessage = "An error occurred while declining the donation appointment.";
            e.printStackTrace(); // Log the full error for debugging.
        }
        
        // --- 3. Redirect Back to the Dashboard ---
        // Redirecting to the servlet ensures the page reloads with the latest data from the database.
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
}

