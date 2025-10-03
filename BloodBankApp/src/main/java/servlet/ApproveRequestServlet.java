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
import java.sql.SQLException;

/**
 * ✅ FINAL FIX: Handles the approval of a patient's blood request.
 * This servlet now sets the final status to "FULFILLED" for a better
 * patient experience.
 */
@WebServlet("/approve-request")
public class ApproveRequestServlet extends HttpServlet {

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
            Request req = RequestDAO.getRequestById(requestId);

            if (req == null) {
                errorMessage = "The requested record could not be found.";
            } else {
                boolean stockAvailable = StockDAO.isStockAvailable(hospital.getId(), req.getBloodGroup(), req.getUnits());

                if (stockAvailable) {
                    // If stock is available, proceed with fulfillment
                    StockDAO.useInventoryBags(hospital.getId(), req.getBloodGroup(), req.getUnits());
                    
                    // ✅ FIXED: Update status to FULFILLED for a consistent patient view
                    RequestDAO.updateRequestStatus(requestId, "FULFILLED");
                    RequestDAO.logRequestAction(requestId, hospital.getId(), "APPROVED"); // Internal log remains "APPROVED"
                    
                    // ✅ FIXED: Update the patient-facing tracking message
                    RequestDAO.updateTrackingStatus(requestId, "Your request has been fulfilled by " + hospital.getName() + ".");
                    
                    successMessage = "Request fulfilled. The stock has been updated.";
                } else {
                    errorMessage = "Approval failed: Insufficient stock for " + req.getBloodGroup() + ".";
                }
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid request ID.";
            e.printStackTrace();
        } catch (SQLException se) {
            errorMessage = "A database error occurred. Please check logs.";
            se.printStackTrace();
        } catch (Exception e) {
            errorMessage = "An unexpected error occurred while processing the request.";
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