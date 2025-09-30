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
 * ✅ FINAL VERSION: Handles the secure receipt of an in-transit blood bag.
 * This servlet includes a critical security check to verify that the logged-in hospital
 * is the legitimate owner of the bag before updating its status to 'CLEARED'.
 */
@WebServlet("/receive-transfer")
public class ReceiveTransferServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        // 1. Standard security check to ensure a hospital is logged in.
        if (hospital == null) {
            response.sendRedirect("hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            int bagId = Integer.parseInt(request.getParameter("bagId"));
            int hospitalId = hospital.getId();

            // 2. --- CRITICAL OWNERSHIP CHECK ---
            // Before proceeding, verify that the bag being received is actually assigned
            // to the hospital that is currently logged in.
            int ownerHospitalId = BloodInventoryDAO.getHospitalIdForBag(bagId);

            if (ownerHospitalId == hospitalId) {
                // If the check passes, the hospital is the legitimate owner. Proceed.
                BloodInventoryDAO.updateBagStatus(bagId, "CLEARED");
                successMessage = "Blood bag #" + bagId + " has been successfully received and added to your inventory.";
            } else {
                // If the check fails, this is a security or data consistency issue. Do not proceed.
                errorMessage = "Error: You do not have permission to receive bag #" + bagId + ". The shipment may belong to another hospital.";
            }
            
        } catch (NumberFormatException e) {
            errorMessage = "Invalid bag ID provided in the URL.";
        } catch (Exception e) {
            errorMessage = "A critical error occurred while receiving the bag.";
            e.printStackTrace();
        }
        
        // 3. Redirect back to the dashboard with a success or error message.
        String redirectURL = request.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
}
