package servlet;

import dao.DonationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/clear-notification")
public class ClearNotificationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            int donationId = Integer.parseInt(req.getParameter("donationId"));
            String currentStatus = req.getParameter("status");
            
            // Move the status to a final state so it's no longer a "new" notification
            String newStatus = "APPROVED".equals(currentStatus) ? "COMPLETED" : "CLOSED";
            
            DonationDAO.updateDonationStatus(donationId, newStatus);
            
            // Redirect back to the donor dashboard
            res.sendRedirect(req.getContextPath() + "/donate");

        } catch (Exception e) {
            throw new ServletException("Error clearing notification", e);
        }
    }
}