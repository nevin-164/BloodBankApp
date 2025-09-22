package servlet;

import dao.RequestDAO;
import dao.StockDAO;
import model.Hospital; // Import Hospital model
import model.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder; // Import the URL encoder

@WebServlet("/approve-request")
public class ApproveRequestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }
        // Get the hospital that is approving the request
        Hospital hospital = (Hospital) session.getAttribute("hospital");

        try {
            int requestId = Integer.parseInt(req.getParameter("requestId"));
            Request bloodRequest = RequestDAO.getRequestById(requestId);

            if (bloodRequest != null) {
                // Check stock availability (this uses our new hybrid method)
                boolean stockAvailable = StockDAO.isStockAvailable(hospital.getId(), bloodRequest.getBloodGroup(), bloodRequest.getUnits());

                if (stockAvailable) {
                    // 1. ✅ MODIFIED: Call the new, smarter hybrid method
                    StockDAO.useInventoryBags(hospital.getId(), bloodRequest.getBloodGroup(), bloodRequest.getUnits());
                    
                    // 2. Mark the central request as fulfilled
                    RequestDAO.updateRequestStatus(requestId, "FULFILLED");
                    
                    // 3. (Analytics): Log this action
                    RequestDAO.logRequestAction(requestId, hospital.getId(), "APPROVED"); 
                    
                    // 4. (Patient Tracking): Update the patient-facing tracking status
                    RequestDAO.updateTrackingStatus(requestId, "Approved");
                    
                    // 5. (Stale Data Fix): Redirect to the SERVLET
                    res.sendRedirect(req.getContextPath() + "/hospital-dashboard?success=" + URLEncoder.encode("Request approved and stock updated!", "UTF-8"));
                } else {
                    // Also fix the redirect on the error case.
                    res.sendRedirect(req.getContextPath() + "/hospital-dashboard?error=" + URLEncoder.encode("Not enough stock to approve request.", "UTF-8"));
                }
            } else {
                // Also fix the redirect on the error case.
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard?error=" + URLEncoder.encode("Request not found.", "UTF-8"));
            }
        } catch (Exception e) {
            // Let's send the error to the dashboard page as well
            try {
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard?error=" + URLEncoder.encode("Error approving request.", "UTF-8"));
            } catch (Exception ex) {
                throw new ServletException("Error approving request", e);
            }
        }
    }
}