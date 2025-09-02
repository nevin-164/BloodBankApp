package servlet;

import dao.DonationDAO;
import dao.HospitalDAO;
import dao.UserDAO; // ✅ ADDED: Import UserDAO for the eligibility check
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

    // This method shows the donation form or appointment details
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"DONOR".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect("login.jsp");
            return;
        }
        User donor = (User) session.getAttribute("user");
        
        try {
            Donation appointment = DonationDAO.getPendingAppointmentForDonor(donor.getId());
            if (appointment != null) {
                req.setAttribute("appointment", appointment);
            } else {
                List<Hospital> hospitals = HospitalDAO.getAllHospitals();
                req.setAttribute("hospitals", hospitals);
            }
            req.getRequestDispatcher("donor.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException("Error loading donor page data", e);
        }
    }

 // In your DonationServlet.java, replace the doPost method.

 // In your DonationServlet.java, replace the doPost method.

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User donor = (User) session.getAttribute("user");

        try {
            // Eligibility Check remains the same...
            java.time.LocalDate today = java.time.LocalDate.now();
            java.sql.Date nextEligibleDate = UserDAO.getNextEligibleDate(donor.getId());

            if (nextEligibleDate != null && today.isBefore(nextEligibleDate.toLocalDate())) {
                req.setAttribute("errorMessage", "You are not yet eligible to donate. Your next eligible date is " + nextEligibleDate);
                doGet(req, res);
                return;
            }
            
            // ✅ MODIFIED: Read the new date parameter from the form.
            int hospitalId = Integer.parseInt(req.getParameter("hospitalId"));
            int units = Integer.parseInt(req.getParameter("units")); // Added back for consistency
            java.sql.Date appointmentDate = java.sql.Date.valueOf(req.getParameter("appointmentDate"));

            DonationDAO.createDonationRequest(donor.getId(), hospitalId, units, appointmentDate);
            
            res.sendRedirect(req.getContextPath() + "/donate?success=Appointment+requested!");

        } catch (Exception e) {
            throw new ServletException("Error creating donation appointment", e);
        }
    }
}