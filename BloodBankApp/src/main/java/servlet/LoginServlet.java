package servlet;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = UserDAO.findByEmailAndPassword(email, password);

            if (user == null) {
                req.setAttribute("msg", "Invalid credentials");
                req.getRequestDispatcher("login.jsp").forward(req, res);
                return;
            }

            // Invalidate any old session to prevent session fixation attacks
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create a new session for the user
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("user", user);

            // Redirect based on the user's role
            switch (user.getRole()) {
                case "ADMIN":
                    res.sendRedirect("admin.jsp");
                    break;
                case "DONOR":
                    // Forward to DonationServlet to load hospital data for the form
                    req.getRequestDispatcher("/donate").forward(req, res);
                    break;
                case "PATIENT":
                    res.sendRedirect("patient.jsp");
                    break;
                default:
                    res.sendRedirect("login.jsp?error=Unknown+role");
                    break;
            }
        } catch (Exception e) {
            throw new ServletException("Login error", e);
        }
    }
}