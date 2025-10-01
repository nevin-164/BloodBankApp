package servlet;

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
 * ✅ FINAL FIX: Restores the original "soft decline" functionality.
 * This servlet now only logs that the current hospital has declined the request.
 * It then triggers a check to see if ALL hospitals have declined it before updating
 * the final status for the patient.
 */
@WebServlet("/decline-request")
public class DeclineRequestServlet extends HttpServlet {

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
            
            // Step 1: Log that THIS hospital has declined the request.
            RequestDAO.logRequestAction(requestId, hospital.getId(), "DECLINED");

            // Step 2: Check if this was the last hospital. If so, update the global status.
            RequestDAO.checkAndFinalizeRequestStatus(requestId);
            
            successMessage = "The request has been removed from your dashboard.";
            
        } catch (NumberFormatException e) {
            errorMessage = "Invalid request ID provided.";
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage = "An error occurred while processing the decline request.";
            e.printStackTrace();
        }

        String redirectURL = request.getContextPath() + "/hospital-dashboard";
        if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        } else {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
}