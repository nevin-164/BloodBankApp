package servlet;

import dao.UserDAO;
import model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminViewUsersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Security Check: Ensure only a logged-in ADMIN can access this page
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Fetch the list of users from the DAO
            List<User> userList = UserDAO.getAllUsers();

            // Set the list as an attribute for the JSP to use
            if (userList == null || userList.isEmpty()) {
                request.setAttribute("message", "No registered users were found.");
            } else {
                request.setAttribute("users", userList);
            }

            // Forward the request to the JSP page for display
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/adminUsers.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: Unable to load user data.");
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/adminUsers.jsp");
            rd.forward(request, response);
        }
    }
}
