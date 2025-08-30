package servlet;

import dao.HospitalDAO;
import model.Hospital;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;

@WebServlet("/hospital-login")
public class HospitalLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            Hospital h = HospitalDAO.findByEmailAndPassword(email,password);
            if(h == null) {
                req.setAttribute("msg","Invalid credentials");
                req.getRequestDispatcher("hospital-login.jsp").forward(req,res);
                return;
            }

            HttpSession session = req.getSession(false);
            if(session != null) session.invalidate();

            session = req.getSession(true);
            session.setAttribute("hospital",h);
            res.sendRedirect("hospital-dashboard.jsp");

        } catch(Exception e) {
            throw new ServletException(e);
        }
    }
}
