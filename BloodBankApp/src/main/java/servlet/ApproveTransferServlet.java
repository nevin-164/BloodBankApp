package servlet;

import dao.StockTransferDAO;
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
 * ✅ DEFINITIVE FINAL VERSION: Handles the approval or rejection of stock transfers.
 * This servlet correctly uses the POST method and calls a safe, transactional DAO function
 * that guarantees stock is available before approving a transfer.
 */
@WebServlet("/approve-transfer")
public class ApproveTransferServlet extends HttpServlet {

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
            int transferId = Integer.parseInt(request.getParameter("transferId"));
            String status = request.getParameter("status");

            if ("APPROVED".equals(status)) {
                // ✅ CRITICAL FIX: Call the new, safe transactional method.
                // This method will only succeed if the hospital has enough stock.
                StockTransferDAO.approveTransferTransaction(transferId, hospital.getId());
                successMessage = "Transfer request #" + transferId + " has been successfully approved and processed.";
            } else { // Handle DECLINED
                StockTransferDAO.updateTransferStatus(transferId, "DECLINED");
                successMessage = "Transfer request #" + transferId + " has been declined.";
            }

        } catch (NumberFormatException e) {
            errorMessage = "Invalid transfer ID provided.";
            e.printStackTrace();
        } catch (SQLException e) {
            // This specific error message is thrown by our new method if stock is insufficient.
            errorMessage = "Could not approve transfer: " + e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage = "An unexpected error occurred: " + e.getMessage();
            e.printStackTrace();
        }
        
        // Use URL parameters for reliable feedback on redirect
        String redirectURL = request.getContextPath() + "/hospital-dashboard";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
}