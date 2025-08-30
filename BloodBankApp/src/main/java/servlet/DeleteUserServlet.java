package servlet;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/deleteUser")
public class DeleteUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Security Check for ADMIN role
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            
            // ✅ CLEAN: Call the single DAO method to handle all deletions
            UserDAO.deleteUser(userId);

            // Redirect to the user list with a success message
            response.sendRedirect(request.getContextPath() + "/admin/users?success=User+deleted+successfully");
        } catch (Exception e) {
            // Forward any errors to be handled
            throw new ServletException("Error during user deletion", e);
        }
    }
}
