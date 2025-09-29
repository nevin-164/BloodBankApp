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

/**
 * This servlet handles the final step of the inter-hospital stock transfer.
 * It is responsible for securely receiving an in-transit blood bag and updating
 * its status to 'CLEARED', officially adding it to the receiving hospital's inventory.
 */
@WebServlet("/receive-transfer")
public class ReceiveTransferServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        // Standard security check to ensure a hospital is logged in.
        if (hospital == null) {
            response.sendRedirect("hospital-login.jsp");
            return;
        }

        try {
            int bagId = Integer.parseInt(request.getParameter("bagId"));
            int hospitalId = hospital.getId();

            // --- CRITICAL SECURITY CHECK ---
            // Before proceeding, we verify that the bag being received is actually assigned
            // to the hospital that is currently logged in.
            int ownerHospitalId = BloodInventoryDAO.getHospitalIdForBag(bagId);

            if (ownerHospitalId == hospitalId) {
                // If the check passes, the hospital is the legitimate owner. Proceed.
                BloodInventoryDAO.updateBagStatus(bagId, "CLEARED");
                session.setAttribute("successMessage", "Blood bag #" + bagId + " has been successfully received and added to your cleared inventory.");
            } else {
                // If the check fails, this is a security or data consistency issue.
                // We do not proceed and inform the user of the error.
                session.setAttribute("errorMessage", "Error: You do not have permission to receive bag #" + bagId + ". The shipment may belong to another hospital.");
            }
            
            // Redirect back to the dashboard to show the updated inventory lists.
            response.sendRedirect("hospital-dashboard");

        } catch (NumberFormatException e) {
            // This handles cases where the bagId in the URL is not a valid number.
            session.setAttribute("errorMessage", "Invalid bag ID provided.");
            response.sendRedirect("hospital-dashboard");
        } catch (Exception e) {
            // This is a general catch-all for any other unexpected errors (e.g., database connection issues).
            e.printStackTrace();
            session.setAttribute("errorMessage", "A critical error occurred while receiving the bag: " + e.getMessage());
            response.sendRedirect("hospital-dashboard");
        }
    }
}

