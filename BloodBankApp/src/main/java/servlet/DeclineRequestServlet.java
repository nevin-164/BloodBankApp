package servlet;

import dao.RequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/decline-request")
public class DeclineRequestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            int requestId = Integer.parseInt(req.getParameter("requestId"));
            RequestDAO.updateRequestStatus(requestId, "DECLINED");
            res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?success=Request+" + requestId + "+has+been+declined.");
        } catch (Exception e) {
            throw new ServletException("Error declining request", e);
        }
    }
}