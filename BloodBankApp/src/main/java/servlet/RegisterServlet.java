package servlet;

import dao.UserDAO;
import dao.DonationDAO;
import dao.StockDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;



@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        String bloodGroup = req.getParameter("blood_group"); // may be null for patients/admin

        if (name == null || email == null || password == null || role == null) {
            req.setAttribute("msg", "Please fill all required fields.");
            req.getRequestDispatcher("register.jsp").forward(req, res);
            return;
        }

        try {
            UserDAO.insert(name, email, password, role, bloodGroup);
            res.sendRedirect("login.jsp");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
