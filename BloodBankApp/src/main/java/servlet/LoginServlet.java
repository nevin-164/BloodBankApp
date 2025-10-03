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
        String redirectURL = req.getParameter("redirectURL");

        try {
            User user = UserDAO.findByEmailAndPassword(email, password);

            if (user == null) {
                // If login fails, forward back to the login page with an error message.
                req.setAttribute("msg", "Invalid credentials");
                req.getRequestDispatcher("login.jsp").forward(req, res);
                return;
            }

            // Invalidate any old session to prevent session fixation attacks.
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create a new session for the authenticated user.
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("user", user);
            
            // ✅ UPDATED: Set a personalized welcome message in the session.
            newSession.setAttribute("welcomeMessage", "Welcome back, " + user.getName() + "!");

            // Check for a valid redirect URL before checking the role.
            if (redirectURL != null && !redirectURL.isEmpty() && !redirectURL.equals("null")) {
                // A valid URL was passed from a page like /community. Send the user back.
                res.sendRedirect(redirectURL);
                return; // End execution here.
            }

            // If no redirectURL, fall back to the default role-based redirection.
            switch (user.getRole()) {
                case "ADMIN":
                    res.sendRedirect(req.getContextPath() + "/admin.jsp");
                    break;
                case "DONOR":
                    // Assuming /donate servlet correctly forwards to the donor dashboard JSP.
                    res.sendRedirect(req.getContextPath() + "/donor.jsp");
                    break;
                case "PATIENT":
                    res.sendRedirect(req.getContextPath() + "/patient.jsp");
                    break;
                default:
                    // Fallback for an unknown role.
                    res.sendRedirect(req.getContextPath() + "/login.jsp?error=Unknown+role");
                    break;
            }
        } catch (Exception e) {
            // Log the exception and throw a ServletException for container handling.
            e.printStackTrace(); // Good for debugging
            throw new ServletException("An error occurred during the login process.", e);
        }
    }
}