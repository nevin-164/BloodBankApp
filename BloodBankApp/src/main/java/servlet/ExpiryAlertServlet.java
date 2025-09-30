package servlet;

import dao.DonationDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/alerts")
public class ExpiryAlertServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Handles the display of blood units nearing their expiration date.
     * This servlet is a critical administrative tool for inventory management.
     * It performs a security check to ensure only admins can access it, fetches the
     * relevant data from the DAO, and forwards it to the JSP for rendering.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        // --- 1. Security Check ---
        // Ensure that a user is logged in and that they have the 'ADMIN' role.
        HttpSession session = req.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            // If the user is not an admin, deny access and redirect to the login page.
            res.sendRedirect(req.getContextPath() + "/login.jsp?error=Access+Denied");
            return;
        }

        // --- 2. Fetch Expiry Data ---
        try {
            // Call the DAO to get a list of all blood units expiring within the next 7 days.
            List<String[]> expiringUnits = DonationDAO.expiringWithinDays(7);
            
            // Set the fetched list as a request attribute so the JSP can access and display it.
            req.setAttribute("expiringUnits", expiringUnits);
            
            // --- 3. Forward to the View ---
            // Forward the request to the JSP page responsible for rendering the alerts table.
            req.getRequestDispatcher("/alerts.jsp").forward(req, res);

        } catch (Exception e) {
            // If any database error occurs, wrap it in a ServletException to be handled by the server.
            e.printStackTrace(); // Log the full error for debugging.
            throw new ServletException("Error loading expiry alerts", e);
        }
    }
}
