package servlet;

import dao.UserDAO;
import model.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User u = UserDAO.findByEmailAndPassword(email, password);
            if (u == null) {
                req.setAttribute("msg", "Invalid credentials");
                req.getRequestDispatcher("login.jsp").forward(req, res);
                return;
            }

            // Invalidate old session for security
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Create a new session
            session = req.getSession(true);
            session.setAttribute("user", u);

            switch (u.getRole()) {
                case "DONOR":
                    res.sendRedirect("donor.jsp");
                    break;
                case "PATIENT":
                    res.sendRedirect("patient.jsp");
                    break;
                default:
                    res.sendRedirect("admin.jsp");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
