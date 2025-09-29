package servlet;

import dao.BloodInventoryDAO;
import model.Hospital;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handles the form submission for manually removing cleared stock.
 * This is for correcting inventory, or removing expired or contaminated units.
 */
public class ManualRemoveStockServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        if (hospital == null) {
            response.sendRedirect("hospital-login.jsp");
            return;
        }

        try {
            String bloodGroup = request.getParameter("bloodGroup");
            int unitsToRemove = Integer.parseInt(request.getParameter("units"));

            // Call the DAO method to remove the oldest cleared bags first.
            int unitsActuallyRemoved = BloodInventoryDAO.manuallyRemoveClearedBags(hospital.getId(), bloodGroup, unitsToRemove);

            if (unitsActuallyRemoved > 0) {
                 session.setAttribute("successMessage", "Successfully removed " + unitsActuallyRemoved + " unit(s) of " + bloodGroup + " from your inventory.");
            } else {
                 session.setAttribute("errorMessage", "Could not remove stock. No cleared units of " + bloodGroup + " were found.");
            }
           
            response.sendRedirect("hospital-dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error removing stock: " + e.getMessage());
            response.sendRedirect("hospital-dashboard");
        }
    }
}
