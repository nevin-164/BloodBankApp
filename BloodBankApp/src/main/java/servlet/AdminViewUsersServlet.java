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

@WebServlet("/admin/users")   // 👉 Access at http://localhost:8080/BloodBankApp/admin/users
public class AdminViewUsersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;  // ✅ Fixes the warning

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // ✅ SESSION CHECK: ensure only logged-in ADMIN can access
        HttpSession session = request.getSession(false);
        User sessionUser = null;
        if (session != null) {
            sessionUser = (User) session.getAttribute("user");
        }
        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Fetch users from DAO
            List<User> userList = UserDAO.getAllUsers();

            // Safety: handle empty/null list
            if (userList == null || userList.isEmpty()) {
                request.setAttribute("message", "No users found in the system.");
            } else {
                request.setAttribute("users", userList);
            }

            // Forward to JSP inside WEB-INF (not directly accessible)
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/adminUsers.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            // Log the exception stack trace for debugging
            e.printStackTrace();

            // Set error message attribute for JSP (optional)
            request.setAttribute("errorMessage", "Unable to load users at this time.");

            // Forward to an error page or show error message in the same JSP
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/adminUsers.jsp");
            rd.forward(request, response);
        }
    }
}
