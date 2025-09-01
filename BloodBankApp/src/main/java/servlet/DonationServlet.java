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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User donor = (User) session.getAttribute("user");

        try {
            // ✅ MODIFIED: Eligibility check now uses the date from the session object
            java.time.LocalDate today = java.time.LocalDate.now();
            java.sql.Date nextEligibleDate = donor.getNextEligibleDate(); // Get date from the user object

            if (nextEligibleDate != null && today.isBefore(nextEligibleDate.toLocalDate())) {
                // If the donor is not eligible, we don't need to do anything here.
                // The JSP will handle displaying the message.
                res.sendRedirect(req.getContextPath() + "/donate");
                return;
            }
            
            // If the donor is eligible, proceed with creating the appointment request.
            int hospitalId = Integer.parseInt(req.getParameter("hospital_id"));
            int units = Integer.parseInt(req.getParameter("units"));

            DonationDAO.createDonationRequest(donor.getId(), hospitalId, units);
            
            res.sendRedirect(req.getContextPath() + "/donate?success=Appointment+requested!");

        } catch (Exception e) {
            throw new ServletException("Error creating donation appointment", e);
        }
    }
}