package servlet;

import dao.DonationDAO;
import model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.List;

@WebServlet("/alerts")
public class ExpiryAlertServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        // Security Check to ensure only an admin can access this page
        HttpSession session = req.getSession(false);
        // ✅ UPDATED: A slightly cleaner way to check the user and their role
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp?error=Access+Denied");
            return;
        }

        try {
            List<String[]> soon = DonationDAO.expiringWithinDays(7);
            req.setAttribute("expiring", soon);
            req.getRequestDispatcher("alerts.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException("Error loading expiry alerts", e);
        }
    }
}