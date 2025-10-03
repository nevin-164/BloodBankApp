package servlet;

import dao.DonationDAO;
import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;

/**
 * ✅ FINAL FIX: Handles the creation of a new donation appointment.
 * This servlet now performs a critical eligibility check using the definitive UserDAO method.
 */
@WebServlet("/donate")
public class DonationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            int userId = user.getId();
            
            // ✅ CRITICAL FIX: Check donor's eligibility before proceeding.
            if (!UserDAO.isDonorEligible(userId)) {
                errorMessage = "You are not yet eligible to make a new donation. Please check your cooldown period.";
            } else {
                int hospitalId = Integer.parseInt(request.getParameter("hospitalId"));
                int units = Integer.parseInt(request.getParameter("units"));
                Date appointmentDate = Date.valueOf(request.getParameter("appointmentDate"));

                // Additional check for existing pending appointments
                if (DonationDAO.getPendingAppointmentForDonor(userId) != null) {
                    errorMessage = "You already have a pending appointment and cannot book another.";
                } else {
                    DonationDAO.createDonationAppointment(userId, hospitalId, units, appointmentDate);
                    successMessage = "Your donation appointment has been successfully scheduled!";
                }
            }

        } catch (Exception e) {
            errorMessage = "An error occurred while scheduling your appointment: " + e.getMessage();
            e.printStackTrace();
        }

        String redirectURL = request.getContextPath() + "/donor.jsp";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null || !"DONOR".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        req.getRequestDispatcher("donor.jsp").forward(req, res);
    }
}