package servlet;

import dao.StockDAO;
import model.Hospital;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * ✅ DEFINITIVE FINAL VERSION: Handles the manual removal of stock from the hospital dashboard.
 * This servlet calls the new intelligent transaction method in StockDAO to ensure
 * stock is removed correctly from both the physical inventory and the manual ledger.
 */
@WebServlet("/manual-remove-stock")
public class ManualRemoveStockServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            String bloodGroup = request.getParameter("bloodGroup");
            int unitsToRemove = Integer.parseInt(request.getParameter("units"));
            int hospitalId = hospital.getId();

            if (unitsToRemove <= 0) {
                errorMessage = "Number of units to remove must be positive.";
            } else {
                // Call the new intelligent method to remove stock
                boolean success = StockDAO.removeStockPrioritizingInventory(hospitalId, bloodGroup, unitsToRemove);
                
                if (success) {
                    successMessage = unitsToRemove + " units of " + bloodGroup + " stock successfully removed.";
                } else {
                    errorMessage = "Not enough stock available to remove " + unitsToRemove + " units of " + bloodGroup + ".";
                }
            }

        } catch (NumberFormatException e) {
            errorMessage = "Invalid number format for units.";
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage = "A critical database error occurred during stock removal: " + e.getMessage();
            e.printStackTrace();
        }
        
        // Redirect back to the dashboard with a message
        String redirectURL = request.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
}