package servlet;

import dao.StockDAO;
import dao.DBUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.*;


@WebServlet("/request-blood")
public class RequestBloodServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String patientIdStr = req.getParameter("patient_id");
        String bg = req.getParameter("blood_group");
        String unitsStr = req.getParameter("units");

        if (patientIdStr == null || bg == null || unitsStr == null) {
            req.setAttribute("msg", "Missing parameters.");
            req.getRequestDispatcher("patient.jsp").forward(req, res);
            return;
        }

        try {
            int patientId = Integer.parseInt(patientIdStr);
            int units = Integer.parseInt(unitsStr);

            if (units <= 0) {
                req.setAttribute("msg", "Units must be positive.");
                req.getRequestDispatcher("patient.jsp").forward(req, res);
                return;
            }

            try (Connection con = DBUtil.getConnection()) {
                con.setAutoCommit(false);

                int requestId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO requests(patient_id,blood_group,units_requested,status) VALUES(?,?,?,'PENDING')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, patientId);
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

                boolean ok = StockDAO.takeUnits(bg, units);

                try (PreparedStatement upd = con.prepareStatement(
                        "UPDATE requests SET status=? WHERE request_id=?")) {
                    upd.setString(1, ok ? "FULFILLED" : "PENDING");
                    upd.setInt(2, requestId);
                    upd.executeUpdate();
                }

                con.commit();

                req.setAttribute("msg", ok ? "Request fulfilled ✅" : "Insufficient stock. Request pending ⏳");
                req.getRequestDispatcher("patient.jsp").forward(req, res);
            }

        } catch (NumberFormatException e) {
            req.setAttribute("msg", "Invalid number format.");
            req.getRequestDispatcher("patient.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
