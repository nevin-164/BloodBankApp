package servlet;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;

@WebServlet("/admin/editUser")
public class EditUserServlet extends HttpServlet {
    
    // This method shows the form to edit a user
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Security check for ADMIN role
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            // ✅ EFFICIENT: Fetch only the specific user needed for editing
            User user = UserDAO.getUserById(userId);

            if (user != null) {
                request.setAttribute("user", user);
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/editUser.jsp");
                rd.forward(request, response);
            } else {
                // If user not found, redirect back to the user list
                response.sendRedirect(request.getContextPath() + "/admin/users?error=User+not+found");
            }
        } catch (Exception e) {
            throw new ServletException("Error loading user for editing", e);
        }
    }

    // This method processes the form submission
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Security check for ADMIN role
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Get form parameters
            int userId = Integer.parseInt(request.getParameter("userId"));
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String bg = request.getParameter("bloodGroup");

            // ✅ Call the newly created updateUser method in the DAO
            UserDAO.updateUser(userId, name, email, bg);

            // Redirect back to the user list with a success message
            response.sendRedirect(request.getContextPath() + "/admin/users?success=User+updated+successfully");
        } catch (Exception e) {
            throw new ServletException("Error updating user", e);
        }
    }
}