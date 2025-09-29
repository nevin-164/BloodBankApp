package servlet;

import dao.BloodInventoryDAO;
import dao.StockTransferDAO;
import model.Hospital;
import model.StockTransfer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/approve-transfer")
public class ApproveTransferServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        if (hospital == null) {
            response.sendRedirect("hospital-login.jsp");
            return;
        }

        try {
            int transferId = Integer.parseInt(request.getParameter("transferId"));
            String status = request.getParameter("status");

            if ("APPROVED".equals(status)) {
                StockTransfer transfer = StockTransferDAO.getTransferById(transferId);
                int unitsRequested = transfer.getUnits();
                String bloodGroup = transfer.getBloodGroup();
                int supplyingHospitalId = transfer.getSupplyingHospitalId();

                // --- THIS IS THE CRITICAL LOGIC FIX ---
                // We now check ONLY for real, CLEARED bags, ignoring the manual stock ledger for transfers.
                int availableBags = BloodInventoryDAO.getClearedBagCount(supplyingHospitalId, bloodGroup);

                if (availableBags >= unitsRequested) {
                    // If the check passes, we are 100% confident the transfer can be fully completed.
                    int bagsTransferred = BloodInventoryDAO.transferBags(
                        supplyingHospitalId, 
                        transfer.getRequestingHospitalId(), 
                        bloodGroup, 
                        unitsRequested
                    );

                    if (bagsTransferred > 0) {
                        StockTransferDAO.updateTransferStatus(transferId, "APPROVED");
                        session.setAttribute("successMessage", "Transfer of " + bagsTransferred + " traceable units approved and now in transit.");
                    } else {
                        // This case is a failsafe for unexpected database issues.
                        session.setAttribute("errorMessage", "An unexpected error occurred during the transfer operation. Please check server logs.");
                    }
                } else {
                    // If the check fails, we stop immediately and provide a clear, informative error message.
                    session.setAttribute("errorMessage", 
                        "Approval failed: Not enough CLEARED bags are available for transfer. Required: " + unitsRequested + 
                        ", Currently available for transfer: " + availableBags + ". Note: Manually added stock cannot be transferred.");
                }
            } else if ("DECLINED".equals(status)) {
                StockTransferDAO.updateTransferStatus(transferId, "DECLINED");
                session.setAttribute("successMessage", "Transfer request has been successfully declined.");
            }

            response.sendRedirect("hospital-dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "A critical error occurred while processing the transfer: " + e.getMessage());
            response.sendRedirect("hospital-dashboard");
        }
    }
}

