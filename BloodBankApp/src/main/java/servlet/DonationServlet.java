package servlet;

import dao.DonationDAO;
import dao.HospitalDAO;
import dao.UserDAO;
import model.Donation;
import model.Hospital;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/donate")
public class DonationServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Handles GET requests to the /donate URL.
     * This method is responsible for displaying the donor dashboard, which includes
     * either the form to book an appointment or the details of an existing pending appointment.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Security check: Ensure a logged-in user is a DONOR.
        if (session == null || session.getAttribute("user") == null || !"DONOR".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User donor = (User) session.getAttribute("user");

        try {
            // This logic is now handled by the donor.jsp itself, but pre-loading here is fine.
            Donation appointment = DonationDAO.getPendingAppointmentForDonor(donor.getId());
            if (appointment != null) {
                // If the donor has a pending appointment, set it as an attribute to be displayed.
                req.setAttribute("appointment", appointment);
            } else {
                // Otherwise, provide a list of hospitals for the appointment booking form.
                List<Hospital> hospitals = HospitalDAO.getAllHospitals();
                req.setAttribute("hospitals", hospitals);
            }
            // Forward the request to the JSP to render the page.
            req.getRequestDispatcher("donor.jsp").forward(req, res);
        } catch (Exception e) {
            // Proper error handling
            throw new ServletException("Error loading donor page data", e);
        }
    }

    /**
     * ✅ FINAL VERSION: Handles POST requests to the /donate URL.
     * This method processes the form submission for requesting a new donation appointment.
     * It includes an eligibility check and calls the corrected DAO method.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Security check
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User donor = (User) session.getAttribute("user");

        try {
            // --- Eligibility Check ---
            java.time.LocalDate today = java.time.LocalDate.now();
            java.sql.Date nextEligibleDate = UserDAO.getNextEligibleDate(donor.getId());

            if (nextEligibleDate != null && today.isBefore(nextEligibleDate.toLocalDate())) {
                req.setAttribute("errorMessage", "You are not yet eligible to donate. Your next eligible date is " + nextEligibleDate);
                // If not eligible, forward back to the form with an error message.
                doGet(req, res);
                return;
            }

            // --- Process Appointment Request ---
            int hospitalId = Integer.parseInt(req.getParameter("hospitalId"));
            int units = Integer.parseInt(req.getParameter("units"));
            java.sql.Date appointmentDate = java.sql.Date.valueOf(req.getParameter("appointmentDate"));

            // ✅ FIXED: Calling the corrected and renamed method in the DAO.
            // This ensures the donation_date is not set prematurely.
            DonationDAO.createDonationAppointment(donor.getId(), hospitalId, units, appointmentDate);

            // Redirect to the donor page with a success message.
            res.sendRedirect(req.getContextPath() + "/donate?success=Appointment+requested!");

        } catch (NumberFormatException e) {
            // Handle cases where form data might be invalid
            req.setAttribute("errorMessage", "Invalid form data submitted.");
            doGet(req, res);
        } catch (Exception e) {
            // General error handling
            throw new ServletException("Error creating donation appointment", e);
        }
    }
}