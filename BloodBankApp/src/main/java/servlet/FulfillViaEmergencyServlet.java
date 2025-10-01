package servlet;

import dao.EmergencyDonorDAO;
import dao.RequestDAO;
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
 * ✅ FINAL VERSION: Handles fulfilling a patient request via emergency donors.
 * This version now processes selected donor IDs from the dashboard, updates
 * the request status, and puts the selected donors on a cooldown period.
 */
@WebServlet("/fulfill-via-emergency")
public class FulfillViaEmergencyServlet extends HttpServlet {

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
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String[] selectedDonors = request.getParameterValues("donorId");

            if (selectedDonors == null || selectedDonors.length == 0) {
                errorMessage = "No emergency donor was selected. Please select at least one donor.";
            } else {
                // 1. Update the patient's request status
                RequestDAO.updateRequestStatus(requestId, "FULFILLED");
                RequestDAO.logRequestAction(requestId, hospital.getId(), "APPROVED");
                RequestDAO.updateTrackingStatus(requestId, "Your request has been fulfilled with the help of an emergency donor.");

                // 2. Put each selected donor on cooldown
                for (String donorIdStr : selectedDonors) {
                    int donorId = Integer.parseInt(donorIdStr);
                    EmergencyDonorDAO.setDonorOnCooldown(donorId);
                }
                
                successMessage = "Patient request fulfilled. The selected donor(s) have been updated.";
            }

        } catch (NumberFormatException e) {
            errorMessage = "Invalid data provided.";
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage = "A critical error occurred while fulfilling the request.";
            e.printStackTrace();
        }
        
        String redirectURL = request.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
}