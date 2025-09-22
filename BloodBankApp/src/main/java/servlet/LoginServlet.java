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
        // ✅ 1. Get the new redirectURL parameter from the hidden form field
        String redirectURL = req.getParameter("redirectURL");

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

            // ✅ 2. Check for a valid redirect URL *before* checking the role
            if (redirectURL != null && !redirectURL.isEmpty() && !redirectURL.equals("null")) {
                // A valid URL was passed from a page like /community. Send the user back.
                res.sendRedirect(redirectURL);
                return; // We are done
            }

            // 3. If no redirectURL, fall back to the default role-based redirect
            switch (user.getRole()) {
                case "ADMIN":
                    res.sendRedirect(req.getContextPath() + "/admin.jsp"); // Added context path
                    break;
                case "DONOR":
                    res.sendRedirect(req.getContextPath() + "/donate"); // This is already correct
                    break;
                case "PATIENT":
                    res.sendRedirect(req.getContextPath() + "/patient.jsp"); // Added context path
                    break;
                default:
                    res.sendRedirect(req.getContextPath() + "/login.jsp?error=Unknown+role"); // Added context path
                    break;
            }
        } catch (Exception e) {
            throw new ServletException("Login error", e);
        }
    }
}