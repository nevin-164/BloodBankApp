package servlet;

import dao.HospitalDAO;
import model.Hospital;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/hospitals")
public class AdminViewHospitalsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        model.User admin = (session != null) ? (model.User)session.getAttribute("user") : null;
        if(admin == null || !"ADMIN".equals(admin.getRole())) {
            res.sendRedirect("login.jsp");
            return;
        }

        try {
            List<Hospital> hospitals = HospitalDAO.getAllHospitals();
            req.setAttribute("hospitals", hospitals);

            // ✅ Forward to JSP directly under webapp
            req.getRequestDispatcher("adminHospitals.jsp").forward(req, res);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }
}

