package servlet;

import dao.EmergencyDonorDAO;
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

/**
 * ✅ FINAL FIX: Handles emergency donor sign-ups.
 * This servlet now performs a critical eligibility check to prevent donors
 * on a cooldown period from signing up as an emergency donor.
 */
@WebServlet("/emergency-signup")
public class EmergencySignupServlet extends HttpServlet {

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

            // ✅ CRITICAL FIX: Check donor's eligibility before allowing emergency sign-up.
            if (!UserDAO.isDonorEligible(userId)) {
                errorMessage = "You are currently on a donation cooldown period and cannot sign up as an emergency donor.";
            } else {
                String bloodGroup = user.getBloodGroup();
                EmergencyDonorDAO.signUp(userId, bloodGroup);
                successMessage = "Thank you! You are now registered as an Emergency Donor for the next 7 days.";
            }

        } catch (Exception e) {
            errorMessage = "An error occurred while signing up: " + e.getMessage();
            e.printStackTrace();
        }

        String redirectURL = request.getContextPath() + "/patient.jsp"; // Or wherever you want to redirect
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
}