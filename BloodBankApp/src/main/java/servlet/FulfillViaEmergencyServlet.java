package servlet;

import dao.RequestDAO;
import dao.UserDAO; // ✅ ADDED: Import UserDAO
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date; // ✅ ADDED: Import for SQL Date
import java.time.LocalDate; // ✅ ADDED: Import for LocalDate

@WebServlet("/fulfill-via-emergency")
public class FulfillViaEmergencyServlet extends HttpServlet {
    private static final int COOLING_DAYS = 90; // Standard 90-day waiting period

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            int requestId = Integer.parseInt(req.getParameter("requestId"));
            // ✅ ADDED: Get the ID of the emergency donor who fulfilled the request
            int donorId = Integer.parseInt(req.getParameter("donorId"));

            // 1. Mark the patient's request as FULFILLED
            RequestDAO.updateRequestStatus(requestId, "FULFILLED");

            // ✅ ADDED: 2. Update the emergency donor's eligibility dates
            LocalDate today = LocalDate.now();
            Date lastDonationDate = Date.valueOf(today);
            Date nextEligibleDate = Date.valueOf(today.plusDays(COOLING_DAYS));
            UserDAO.updateDonationDates(donorId, lastDonationDate, nextEligibleDate);
            
            res.sendRedirect(req.getContextPath() + "/hospital-dashboard.jsp?success=Request+" + requestId + "+fulfilled+via+emergency+donor!");
        } catch (Exception e) {
            throw new ServletException("Error fulfilling request via emergency donor", e);
        }
    }
}