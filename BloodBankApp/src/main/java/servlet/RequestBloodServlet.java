package servlet;

import dao.StockDAO;
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

        // ✅ SECURE: Get the logged-in patient from the session
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null || !"PATIENT".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect("login.jsp?error=Please+login+as+a+patient+to+request+blood.");
            return;
        }
        User patient = (User) session.getAttribute("user");

        // ✅ SECURE: Get blood group from the session, not the form
        String bg = patient.getBloodGroup(); 
        String unitsStr = req.getParameter("units");

        if (unitsStr == null) {
            req.setAttribute("msg", "Missing number of units.");
            req.getRequestDispatcher("patient.jsp").forward(req, res);
            return;
        }

        try {
            int units = Integer.parseInt(unitsStr);
            if (units <= 0) {
                req.setAttribute("msg", "Units must be a positive number.");
                req.getRequestDispatcher("patient.jsp").forward(req, res);
                return;
            }

            try (Connection con = DBUtil.getConnection()) {
                con.setAutoCommit(false);

                // ✅ SECURE: Insert the request using the patient ID from the session
                int requestId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO requests(patient_id,blood_group,units_requested,status) VALUES(?,?,?,'PENDING')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, patient.getId()); // Use patient ID from session
                    ps.setString(2, bg);
                    ps.setInt(3, units);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            requestId = rs.getInt(1);
                        } else {
                            con.rollback();
                            throw new SQLException("Failed to retrieve request ID.");
                        }
                    }
                }

                boolean isFulfilled = StockDAO.takeUnits(bg, units);

                try (PreparedStatement upd = con.prepareStatement(
                        "UPDATE requests SET status=? WHERE request_id=?")) {
                    upd.setString(1, isFulfilled ? "FULFILLED" : "PENDING");
                    upd.setInt(2, requestId);
                    upd.executeUpdate();
                }

                con.commit();
                
                req.setAttribute("msg", isFulfilled ? "Request fulfilled successfully! ✅" : "Request is pending due to insufficient stock. ⏳");
                req.getRequestDispatcher("patient.jsp").forward(req, res);
            }

        } catch (NumberFormatException e) {
            req.setAttribute("msg", "Invalid number format for units.");
            req.getRequestDispatcher("patient.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException("Error processing blood request", e);
        }
    }
}