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
            HospitalDAO.deleteHospital(id);
            // ✅ FIXED: Redirect to the servlet that reloads the hospital list
            res.sendRedirect(req.getContextPath() + "/admin/hospitals?success=Hospital+deleted+successfully");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}