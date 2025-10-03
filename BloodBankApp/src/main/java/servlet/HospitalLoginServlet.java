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
                req.getRequestDispatcher("/hospital-login.jsp").forward(req, res);
                return;
            }

            // Invalidate any old session and create a new one for security
            HttpSession oldSession = req.getSession(false);
            if(oldSession != null) {
                oldSession.invalidate();
            }
            
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("hospital", h);
            
            // ✅ UPDATED: Set a personalized welcome message in the session
            newSession.setAttribute("welcomeMessage", "Welcome, " + h.getName() + "!");
            
            // Redirect to the hospital dashboard servlet to load necessary data
            res.sendRedirect(req.getContextPath() + "/hospital-dashboard");

        } catch (Exception e) {
            e.printStackTrace(); // Good practice for debugging
            throw new ServletException("An error occurred during hospital login.", e);
        }
    }
}