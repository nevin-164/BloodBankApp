package servlet;

import dao.HospitalDAO;
import model.Hospital;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/hospitalList")   // ✅ keep it simple & consistent
public class HospitalListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            // ✅ Fetch hospitals from DAO
            List<Hospital> hospitals = HospitalDAO.getAllHospitals();
            req.setAttribute("hospitals", hospitals);

            // ✅ Capture optional messages from redirect (success/error)
            String success = req.getParameter("success");
            String error = req.getParameter("error");
            if (success != null) {
                req.setAttribute("success", success);
            }
            if (error != null) {
                req.setAttribute("error", error);
            }

            // ✅ Forward to protected JSP (inside WEB-INF)
            req.getRequestDispatcher("/adminHospitals.jsp").forward(req, res);

        } catch (Exception e) {
            throw new ServletException("Error loading hospitals list", e);
        }
    }
}
