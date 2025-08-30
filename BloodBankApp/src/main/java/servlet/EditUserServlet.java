package servlet;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/editUser")
public class EditUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("userId"));
        try {
            User user = null;
            for (User u : UserDAO.getAllUsers()) {
                if (u.getUserId() == userId) {
                    user = u;
                    break;
                }
            }
            if (user != null) {
                request.setAttribute("user", user);
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/editUser.jsp");
                rd.forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("userId"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String bg = request.getParameter("bloodGroup");

        try {
            UserDAO.updateUser(userId, name, email, bg);
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
