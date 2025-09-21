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
import java.net.URLEncoder; // Import the URL encoder

@WebServlet("/decline-request")
public class DeclineRequestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }
        Hospital hospital = (Hospital) session.getAttribute("hospital");

        try {
            int requestId = Integer.parseInt(req.getParameter("requestId"));

            // ✅ MODIFIED: This now records the decline action for this specific hospital
            // without changing the main request's status to 'DECLINED'.
            RequestDAO.declineRequestForHospital(requestId, hospital.getId());

            String successMessage = "Request " + requestId + " has been hidden from your view.";

            // ✅ FIX: Redirect to the SERVLET, not the JSP, to fix stale data.
            res.sendRedirect(req.getContextPath() + "/hospital-dashboard?success=" + URLEncoder.encode(successMessage, "UTF-8"));

        } catch (Exception e) {
            try {
                // Also fix the error redirect
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard?error=" + URLEncoder.encode("Error declining request.", "UTF-8"));
            } catch (Exception ex) {
                throw new ServletException("Error declining request", e);
            }
        }
    }
}