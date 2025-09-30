package servlet;

import dao.DBUtil;
import model.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.*;
import java.net.URLEncoder;

@WebServlet("/request-blood")
public class RequestBloodServlet extends HttpServlet {

    /**
     * ✅ FINAL VERSION: Processes a blood request from either a patient or a donor.
     * This version corrects the parameter name mismatch that was causing the SQL error.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // 1. Security Check: Ensure a user is logged in.
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp?error=" + URLEncoder.encode("Please login to make a request.", "UTF-8"));
            return;
        }
        
        User user = (User) session.getAttribute("user");

        // 2. Role Check: Ensure the user is either a DONOR or a PATIENT.
        if (!"DONOR".equals(user.getRole()) && !"PATIENT".equals(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp?error=" + URLEncoder.encode("Only donors or patients can make requests.", "UTF-8"));
            return;
        }
        
        // 3. Determine the correct page to redirect back to after the request.
        String redirectURL = "patient.jsp"; // Default for patients
        if ("DONOR".equals(user.getRole())) {
            redirectURL = req.getContextPath() + "/donate"; // Donors go back to their dashboard servlet
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            // 4. ✅ FIXED: Read the correct parameter name "bloodGroup" (camelCase) from the form.
            String bloodGroup = req.getParameter("bloodGroup");
            int units = Integer.parseInt(req.getParameter("units"));

            // 5. Input Validation
            if (bloodGroup == null || bloodGroup.trim().isEmpty()) {
                errorMessage = "Blood group cannot be empty.";
            } else if (units <= 0) {
                 errorMessage = "Units must be a positive number.";
            }

            if (!errorMessage.isEmpty()) {
                 res.sendRedirect(redirectURL + "?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
                 return;
            }

            // 6. Database Operation: Insert the new request.
            String sql = "INSERT INTO requests(patient_id, blood_group, units_requested, status, request_date, tracking_status) VALUES(?,?,?,'PENDING',CURDATE(),'Pending')";
            try (Connection con = DBUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, user.getId());
                ps.setString(2, bloodGroup); // This variable now holds the correct value from the form.
                ps.setInt(3, units);
                ps.executeUpdate();
            }
            
            successMessage = "Blood request submitted successfully! It is now pending.";

        } catch (NumberFormatException e) {
            errorMessage = "Invalid number format for units. Please enter a whole number.";
        } catch (Exception e) {
            errorMessage = "A database error occurred while submitting your request.";
            // For developers: Log the full exception to the server console.
            e.printStackTrace(); 
            // We re-throw the exception so the server's error page is displayed for critical failures.
            throw new ServletException("Error submitting blood request", e);
        }
        
        // 7. Final Redirect Logic
        if (!errorMessage.isEmpty()) {
            res.sendRedirect(redirectURL + "?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
        } else {
            res.sendRedirect(redirectURL + "?success=" + URLEncoder.encode(successMessage, "UTF-8"));
        }
    }
}
