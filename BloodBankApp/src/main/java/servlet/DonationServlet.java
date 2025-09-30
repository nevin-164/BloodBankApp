package servlet;

import dao.DonationDAO;
import dao.UserDAO;
import model.Donation;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/donate")
public class DonationServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Handles GET requests for the donor dashboard.
     * Logic is simplified to only handle security and forward to the JSP.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null || !"DONOR".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        req.getRequestDispatcher("donor.jsp").forward(req, res);
    }

    /**
     * ✅ FINAL VERSION: Handles POST requests for creating a new donation appointment.
     * Includes a more robust eligibility check to prevent duplicate appointments.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User donor = (User) session.getAttribute("user");
        String errorMessage = "";

        try {
            // --- Robust Eligibility Check ---
            // 1. Check for an existing active appointment.
            Donation existingAppointment = DonationDAO.getPendingAppointmentForDonor(donor.getId());
            if (existingAppointment != null) {
                errorMessage = "You already have a pending appointment and cannot book another.";
            }

            // 2. Check the donor's cooldown period.
            if (errorMessage.isEmpty()) {
                java.time.LocalDate today = java.time.LocalDate.now();
                // ✅ FIXED: Fetch the full, fresh user object to get the eligibility date.
                // This relies on the corrected UserDAO.getUserById() method.
                User freshDonorData = UserDAO.getUserById(donor.getId());
                java.sql.Date nextEligibleDate = freshDonorData.getNextEligibleDate();

                if (nextEligibleDate != null && today.isBefore(nextEligibleDate.toLocalDate())) {
                    errorMessage = "You are not yet eligible to donate. Your next eligible date is " + nextEligibleDate;
                }
            }
            
            // If any error was found, redirect back to the form with a message.
            if (!errorMessage.isEmpty()) {
                String redirectURL = req.getContextPath() + "/donor.jsp?error=" + URLEncoder.encode(errorMessage, "UTF-8");
                res.sendRedirect(redirectURL);
                return;
            }

            // --- Process Appointment Request ---
            int hospitalId = Integer.parseInt(req.getParameter("hospitalId"));
            int units = Integer.parseInt(req.getParameter("units"));
            java.sql.Date appointmentDate = java.sql.Date.valueOf(req.getParameter("appointmentDate"));

            DonationDAO.createDonationAppointment(donor.getId(), hospitalId, units, appointmentDate);

            String successMessage = "Your appointment has been successfully requested!";
            res.sendRedirect(req.getContextPath() + "/donor.jsp?success=" + URLEncoder.encode(successMessage, "UTF-8"));

        } catch (NumberFormatException e) {
            errorMessage = "Invalid form data was submitted. Please try again.";
            res.sendRedirect(req.getContextPath() + "/donor.jsp?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
        } catch (Exception e) {
            throw new ServletException("Error creating donation appointment", e);
        }
    }
}