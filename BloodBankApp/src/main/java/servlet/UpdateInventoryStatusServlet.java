package servlet;

import dao.BloodInventoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/update-inventory-status")
public class UpdateInventoryStatusServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Get the bag_id and the new status from the URL
        String newStatus = req.getParameter("status");
        String successMessage = "";
        String errorMessage = "";

        try {
            int bagId = Integer.parseInt(req.getParameter("bagId"));

            if ("CLEARED".equals(newStatus)) {
                BloodInventoryDAO.updateBagStatus(bagId, "CLEARED");
                successMessage = "Bag " + bagId + " has been cleared and added to stock.";
            } else {
                // We can add other logic here later, e.g., "DISCARDED"
                errorMessage = "Invalid status update.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Error updating inventory status.";
        }

        // Redirect back to the hospital dashboard
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
}