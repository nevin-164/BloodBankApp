package servlet;

import dao.RequestDAO;
import dao.StockDAO;
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

        try {
            int requestId = Integer.parseInt(req.getParameter("requestId"));
            Request bloodRequest = RequestDAO.getRequestById(requestId);

            if (bloodRequest == null) {
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?error=Request+not+found.");
                return;
            }

            boolean stockAvailable = StockDAO.isStockAvailable(bloodRequest.getBloodGroup(), bloodRequest.getUnits());

            if (stockAvailable) {
                StockDAO.removeStock(bloodRequest.getBloodGroup(), bloodRequest.getUnits());
                RequestDAO.updateRequestStatus(requestId, "FULFILLED");
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?success=Request+" + requestId + "+approved+and+stock+updated!");
            } else {
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?error=Not+enough+stock+to+approve+request+" + requestId);
            }

        } catch (Exception e) {
            throw new ServletException("Error approving request", e);
        }
    }
}