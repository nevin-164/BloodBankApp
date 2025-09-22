package servlet;

import dao.DonationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder; // Import the URL encoder

@WebServlet("/decline-donation")
public class DeclineDonationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // Security check for hospital user
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            int donationId = Integer.parseInt(req.getParameter("donationId"));

            // Use the new DAO method to update the status
            DonationDAO.updateDonationStatus(donationId, "DECLINED");

            successMessage = "Donation appointment " + donationId + " has been declined.";

        } catch (Exception e) {
            errorMessage = "Error declining donation appointment";
            e.printStackTrace();
        }
        
        // ✅ FIX: Redirect to the SERVLET, not the JSP, to fix stale data.
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
}