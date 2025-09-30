package servlet;

import dao.DonationDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/clear-notification")
public class ClearNotificationServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Handles the clearing of a donor's notification.
     * This servlet is called when a donor acknowledges an 'APPROVED' or 'DECLINED' status
     * on their dashboard. It now calls a dedicated, safe method in the DAO to resolve
     * the previous compile error and includes a proper security check.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        // --- 1. Security Check ---
        // Ensure that a user is logged in and that they are a donor.
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null || !"DONOR".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // --- 2. Process the Action ---
        try {
            // Get the ID of the donation whose notification should be cleared.
            int donationId = Integer.parseInt(req.getParameter("donationId"));
            
            // ✅ FIXED: Call the new, dedicated public method for clearing notifications.
            // This resolves the compile error and uses the correct, safe data access logic.
            DonationDAO.clearDonationNotification(donationId);
            
            // Redirect back to the donor dashboard to show the updated status.
            res.sendRedirect(req.getContextPath() + "/donate");

        } catch (Exception e) {
            // If anything goes wrong, log the error and send the user to a safe page.
            e.printStackTrace();
            throw new ServletException("Error clearing notification", e);
        }
    }
}

