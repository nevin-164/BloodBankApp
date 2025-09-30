package servlet;

import dao.BloodInventoryDAO;
import model.Hospital;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * ✅ FINAL VERSION: Handles the manual addition of cleared blood stock by a hospital user.
 * This servlet has been updated to call the correct, more efficient DAO method that
 * accepts the number of units and uses a batch update, resolving the previous compile error
 * and improving performance.
 */
@WebServlet("/manual-add-stock")
public class ManualAddStockServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        // 1. Security Check
        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        // 2. Process the Form Data
        try {
            String bloodGroup = request.getParameter("bloodGroup");
            int units = Integer.parseInt(request.getParameter("units"));

            // Input validation
            if (units <= 0) {
                errorMessage = "Number of units must be a positive number.";
            } else {
                // ✅ FIXED: Make a single, efficient call to the corrected DAO method.
                // The DAO now handles the loop and batch update internally.
                BloodInventoryDAO.manuallyAddClearedBag(hospital.getId(), bloodGroup, units);
                successMessage = units + " units of " + bloodGroup + " blood have been successfully added to your cleared stock.";
            }

        } catch (NumberFormatException e) {
            errorMessage = "Invalid number provided for units.";
        } catch (Exception e) {
            errorMessage = "A critical error occurred while adding stock.";
            e.printStackTrace(); // Log the full error for debugging
        }
        
        // 3. Redirect back to the dashboard with the appropriate message
        String redirectURL = request.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
}
