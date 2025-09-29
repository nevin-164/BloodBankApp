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

/**
 * This is the final, definitive version of the DeclineRequestServlet.
 * It is now fully synchronized with the final RequestDAO and uses the modern
 * session-based "flash message" system for user notifications.
 */
@WebServlet("/decline-request")
public class DeclineRequestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

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

            // ✅ FIXED: Using the single, correct method from the final DAO.
            // This logs that this specific hospital has declined the request.
            RequestDAO.logRequestAction(requestId, hospital.getId(), "DECLINED");

            String successMessage = "Request " + requestId + " has been hidden from your view.";

            // ✅ FIXED: Using the modern "flash message" system for toast notifications.
            session.setAttribute("successMessage", successMessage);
            res.sendRedirect(req.getContextPath() + "/hospital-dashboard");

        } catch (Exception e) {
            e.printStackTrace(); // Log the full error to the console for debugging.
            
            // Also use the session for error messages.
            session.setAttribute("errorMessage", "An error occurred while declining the request.");
            res.sendRedirect(req.getContextPath() + "/hospital-dashboard");
        }
    }
}

