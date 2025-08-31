package servlet;

import dao.DonationDAO;
import dao.StockDAO;
import dao.UserDAO; // ✅ ADDED: Import UserDAO
import model.Donation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date; // ✅ ADDED: Import for SQL Date
import java.time.LocalDate; // ✅ ADDED: Import for LocalDate

@WebServlet("/approve-donation")
public class ApproveDonationServlet extends HttpServlet {
    private static final int COOLING_DAYS = 90; // Standard 90-day waiting period

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            int donationId = Integer.parseInt(req.getParameter("donationId"));
            Donation donation = DonationDAO.getDonationById(donationId);

            if (donation != null) {
                // 1. Update the stock
                StockDAO.addUnits(donation.getBloodGroup(), donation.getUnits());
                // 2. Mark the donation as approved
                DonationDAO.updateDonationStatus(donationId, "APPROVED");

                // ✅ ADDED: Update the donor's eligibility dates
                LocalDate today = LocalDate.now();
                Date lastDonationDate = Date.valueOf(today);
                Date nextEligibleDate = Date.valueOf(today.plusDays(COOLING_DAYS));
                UserDAO.updateDonationDates(donation.getUserId(), lastDonationDate, nextEligibleDate);

                res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?success=Donation+approved+and+stock+updated!");
            } else {
                res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?error=Donation+not+found.");
            }
        } catch (Exception e) {
            throw new ServletException("Error approving donation", e);
        }
    }
}