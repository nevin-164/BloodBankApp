package servlet;

import dao.DonationDAO;
import dao.HospitalDAO;
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

    // This method shows the donation form
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"DONOR".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect("login.jsp");
            return;
        }
        User donor = (User) session.getAttribute("user");
        
        try {
            // Check for an existing appointment
            Donation appointment = DonationDAO.getPendingAppointmentForDonor(donor.getId());
            if (appointment != null) {
                req.setAttribute("appointment", appointment);
            } else {
                // If no appointment, load hospitals for the request form
                List<Hospital> hospitals = HospitalDAO.getAllHospitals();
                req.setAttribute("hospitals", hospitals);
            }
            req.getRequestDispatcher("donor.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException("Error loading donor page data", e);
        }
    }

    // This method processes the appointment request
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User donor = (User) session.getAttribute("user");

        try {
            int hospitalId = Integer.parseInt(req.getParameter("hospital_id"));
            int units = Integer.parseInt(req.getParameter("units"));

            DonationDAO.createDonationRequest(donor.getId(), hospitalId, units);
            
            // Redirect back to the GET method to refresh the page and show the new appointment
            res.sendRedirect(req.getContextPath() + "/donate?success=Appointment+requested!");

        } catch (Exception e) {
            throw new ServletException("Error creating donation appointment", e);
        }
    }
}