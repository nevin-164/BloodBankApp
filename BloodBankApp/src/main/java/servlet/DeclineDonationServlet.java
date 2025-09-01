package servlet;

import dao.DonationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

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

        try {
            int donationId = Integer.parseInt(req.getParameter("donationId"));

            // Use the new DAO method to update the status
            DonationDAO.updateDonationStatus(donationId, "DECLINED");

            // Redirect back to the dashboard with a success message
            res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?success=Donation+appointment+" + donationId + "+has+been+declined.");

        } catch (Exception e) {
            throw new ServletException("Error declining donation appointment", e);
        }
    }
}