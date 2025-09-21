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
            Hospital h = HospitalDAO.findByEmailAndPassword(email, password);
            if (h == null) {
                req.setAttribute("msg", "Invalid credentials. Please try again.");
                // ✅ FIXED: Forwards back to the correct hospital login page.
                req.getRequestDispatcher("/hospital-login.jsp").forward(req, res);
                return;
            }

            // Invalidate any old session and create a new one
            HttpSession oldSession = req.getSession(false);
            if(oldSession != null) oldSession.invalidate();
            
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("hospital", h);
            
            // ✅ MODIFIED: Redirect to the NEW SERVLET's URL, not the JSP file.
            // This ensures all the data is loaded by our new servlet.
            res.sendRedirect(req.getContextPath() + "/hospital-dashboard");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}