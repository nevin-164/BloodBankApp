package servlet;

import dao.HospitalDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/deleteHospital")
public class DeleteHospitalServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("hospitalId"));
        try {
            // Correct DAO method
            HospitalDAO.deleteHospital(id);
            res.sendRedirect("adminHospitals.jsp?success=Hospital deleted successfully");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
