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
import java.net.URLEncoder; // ✅ ADDED: For redirect messages

@WebServlet("/request-blood")
public class RequestBloodServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // 1. ✅ FIX: Modified Security Check
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // User isn't logged in at all
            res.sendRedirect(req.getContextPath() + "/login.jsp?error=" + URLEncoder.encode("Please login to make a request.", "UTF-8"));
            return;
        }
        User user = (User) session.getAttribute("user");

        // Now, we allow BOTH Donors and Patients
        if (!"DONOR".equals(user.getRole()) && !"PATIENT".equals(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp?error=" + URLEncoder.encode("Only donors or patients can make requests.", "UTF-8"));
            return;
        }
        
        // 2. ✅ FIX: Determine where to redirect the user
        // Donors go back to their servlet, Patients go back to their JSP
        String redirectURL = "patient.jsp"; // Default for patients
        if ("DONOR".equals(user.getRole())) {
            redirectURL = req.getContextPath() + "/donate"; // This is the Donor's dashboard servlet
        }

        String successMessage = "";
        String errorMessage = "";

        try {
            String bloodGroup = req.getParameter("blood_group");
            int units = Integer.parseInt(req.getParameter("units"));

            if (units <= 0) {
                 errorMessage = "Units must be a positive number.";
                 // 3. ✅ FIX: Use sendRedirect with an error message
                 res.sendRedirect(redirectURL + "?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
                 return;
            }

            // 4. ✅ FIX: Added the new 'tracking_status' column to the SQL
            String sql = "INSERT INTO requests(patient_id, blood_group, units_requested, status, request_date, tracking_status) VALUES(?,?,?,'PENDING',CURDATE(),'Pending')";
            try (Connection con = DBUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, user.getId()); // Use the generic 'user' object
                ps.setString(2, bloodGroup);
                ps.setInt(3, units);
                ps.executeUpdate();
            }
            
            successMessage = "Blood request submitted successfully! It is now pending.";

        } catch (NumberFormatException e) {
            errorMessage = "Invalid number format for units.";
        } catch (Exception e) {
            errorMessage = "Error submitting blood request.";
            throw new ServletException("Error submitting blood request", e);
        }
        
        // 5. ✅ FIX: Final redirect logic
        if (!errorMessage.isEmpty()) {
            res.sendRedirect(redirectURL + "?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
        } else {
            res.sendRedirect(redirectURL + "?success=" + URLEncoder.encode(successMessage, "UTF-8"));
        }
    }
}