package servlet;

import dao.DonationDAO;
import dao.StockDAO;
import model.Donation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/approve-donation")
public class ApproveDonationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // 1. Security Check: Ensure a hospital user is logged in
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            // 2. Get the ID of the donation to be approved
            int donationId = Integer.parseInt(req.getParameter("donationId"));
            Donation donation = DonationDAO.getDonationById(donationId);

            if (donation != null) {
                // 3. Add the donated units to the blood stock
                StockDAO.addUnits(donation.getBloodGroup(), donation.getUnits());
                
                // 4. Update the donation's status to "APPROVED"
                DonationDAO.updateDonationStatus(donationId, "APPROVED");
                
                // 5. Redirect back to the dashboard with a success message
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?success=Donation+approved+and+stock+updated!");
            } else {
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?error=Donation+not+found.");
            }
        } catch (Exception e) {
            throw new ServletException("Error approving donation", e);
        }
    }
}