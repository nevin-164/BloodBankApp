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
                // ✅ MODIFIED: Check stock availability AT THIS SPECIFIC HOSPITAL
                boolean stockAvailable = StockDAO.isStockAvailable(hospital.getId(), bloodRequest.getBloodGroup(), bloodRequest.getUnits());

                if (stockAvailable) {
                    // ✅ MODIFIED: Deduct stock FROM THIS SPECIFIC HOSPITAL
                    StockDAO.takeUnits(hospital.getId(), bloodRequest.getBloodGroup(), bloodRequest.getUnits());
                    
                    // Mark the central request as fulfilled
                    RequestDAO.updateRequestStatus(requestId, "FULFILLED");
                    res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?success=Request+approved+and+stock+updated!");
                } else {
                    res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?error=Not+enough+stock+to+approve+request.");
                }
            } else {
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?error=Request+not+found.");
            }
        } catch (Exception e) {
            throw new ServletException("Error approving request", e);
        }
    }
}