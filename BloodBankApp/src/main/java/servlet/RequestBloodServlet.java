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

@WebServlet("/request-blood")
public class RequestBloodServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // 1. Security Check: Ensure a patient is logged in
        HttpSession session = req.getSession(false);
        if (session == null || !"PATIENT".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect("login.jsp?error=Please+login+as+a+patient.");
            return;
        }
        User patient = (User) session.getAttribute("user");

        try {
            // ✅ MODIFICATION: Get the selected blood group from the form's dropdown
            String bloodGroup = req.getParameter("blood_group");
            int units = Integer.parseInt(req.getParameter("units"));

            if (units <= 0) {
                 req.setAttribute("msg", "Units must be a positive number.");
                 req.getRequestDispatcher("patient.jsp").forward(req, res);
                 return;
            }

            String sql = "INSERT INTO requests(patient_id, blood_group, units_requested, status, request_date) VALUES(?,?,?,'PENDING',CURDATE())";
            try (Connection con = DBUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, patient.getId());
                // ✅ MODIFICATION: Use the blood group from the form, not the patient's profile
                ps.setString(2, bloodGroup);
                ps.setInt(3, units);
                ps.executeUpdate();
            }
            
            req.setAttribute("msg", "Blood request submitted successfully! It is now pending hospital approval. ✅");
            req.getRequestDispatcher("patient.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            req.setAttribute("msg", "Invalid number format for units.");
            req.getRequestDispatcher("patient.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException("Error submitting blood request", e);
        }
    }
}