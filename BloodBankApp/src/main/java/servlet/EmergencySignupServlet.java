package servlet;

import dao.EmergencyDonorDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/emergency-signup")
public class EmergencySignupServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Handles the emergency donor sign-up process.
     * This has been updated to pass the donor's blood group to the DAO, which
     * is a critical fix for the hospital's emergency contact feature.
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

        // A donor must have a blood group to sign up for emergency donations.
        if (donor.getBloodGroup() == null || donor.getBloodGroup().isEmpty()) {
            String errorMessage = "You must have a blood group set in your profile to become an emergency donor.";
            res.sendRedirect(req.getContextPath() + "/donor.jsp?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        try {
            // ✅ FINAL FIX: Pass both the user ID and their blood group to the DAO.
            EmergencyDonorDAO.signUp(donor.getId(), donor.getBloodGroup());
            
            String successMessage = "Thank you! You are now registered as an emergency donor for one week.";
            res.sendRedirect(req.getContextPath() + "/donor.jsp?success=" + URLEncoder.encode(successMessage, "UTF-8"));

        } catch (Exception e) {
            throw new ServletException("Error signing up for emergency donation", e);
        }
    }
}