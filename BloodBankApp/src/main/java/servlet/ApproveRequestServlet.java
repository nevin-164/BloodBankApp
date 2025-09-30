package servlet;

import dao.RequestDAO;
import dao.StockDAO;
import model.Hospital;
import model.Request;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/approve-request")
public class ApproveRequestServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Processes the approval of a patient's blood request by a hospital.
     * This servlet ensures that the hospital has enough stock before fulfilling the request.
     * It performs four key actions upon successful approval:
     * 1. Deducts the required units from the hospital's inventory.
     * 2. Updates the central request's status to "FULFILLED".
     * 3. Logs the approval action for analytics.
     * 4. Updates the patient-facing status for tracking purposes.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Security check: Ensure a hospital is logged in before proceeding.
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        // Get the hospital object from the session to identify who is approving.
        Hospital hospital = (Hospital) session.getAttribute("hospital");
        String successMessage = "";
        String errorMessage = "";

        try {
            int requestId = Integer.parseInt(req.getParameter("requestId"));
            Request bloodRequest = RequestDAO.getRequestById(requestId);

            if (bloodRequest != null) {
                // STEP 1: Check if the hospital has sufficient stock to fulfill the request.
                boolean isStockAvailable = StockDAO.isStockAvailable(
                    hospital.getId(),
                    bloodRequest.getBloodGroup(),
                    bloodRequest.getUnits()
                );

                if (isStockAvailable) {
                    // STEP 2: If stock is available, use the inventory bags.
                    // This DAO method will handle the logic of selecting the oldest bags first.
                    StockDAO.useInventoryBags(
                        hospital.getId(),
                        bloodRequest.getBloodGroup(),
                        bloodRequest.getUnits()
                    );

                    // STEP 3: Update the central request status to prevent other hospitals from acting on it.
                    RequestDAO.updateRequestStatus(requestId, "FULFILLED");

                    // STEP 4 (Analytics): Log that this specific hospital approved the request.
                    RequestDAO.logRequestAction(requestId, hospital.getId(), "APPROVED");

                    // STEP 5 (Patient Tracking): Update the status the patient sees.
                    RequestDAO.updateTrackingStatus(requestId, "Approved");
                    
                    successMessage = "Request approved successfully and stock has been updated!";
                } else {
                    errorMessage = "Not enough stock available to approve this request.";
                }
            } else {
                errorMessage = "The requested blood drive could not be found. It may have been deleted or fulfilled by another hospital.";
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid request ID provided.";
        } catch (Exception e) {
            errorMessage = "A critical error occurred while approving the request.";
            e.printStackTrace(); // Log the full error for debugging purposes.
        }

        // --- Redirect back to the hospital dashboard with a message ---
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        res.sendRedirect(redirectURL);
    }
}