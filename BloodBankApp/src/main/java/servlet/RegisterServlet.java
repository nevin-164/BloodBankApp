package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        String bloodGroup = req.getParameter("blood_group");
        // ✅ ADDED: Get the new contact number from the form
        String contactNumber = req.getParameter("contact_number");

        try {
            // Server-side validation
            if (!"DONOR".equals(role) && !"PATIENT".equals(role)) {
                req.setAttribute("msg", "Invalid role selected.");
                req.getRequestDispatcher("register.jsp").forward(req, res);
                return;
            }
            
            if (UserDAO.isEmailExists(email)) {
                req.setAttribute("msg", "An account with this email already exists.");
                req.getRequestDispatcher("register.jsp").forward(req, res);
                return;
            }
            
            if ("DONOR".equals(role) && (bloodGroup == null || bloodGroup.isEmpty())) {
                 req.setAttribute("msg", "Donors must provide a blood group.");
                 req.getRequestDispatcher("register.jsp").forward(req, res);
                 return;
            }

            // ✅ UPDATED: Pass the new contactNumber to the DAO
            UserDAO.insert(name, email, password, role, bloodGroup, contactNumber);
            
            res.sendRedirect("login.jsp?success=Registration+successful.+Please+login.");

        } catch (Exception e) {
            throw new ServletException("Error during user registration.", e);
        }
    }
}