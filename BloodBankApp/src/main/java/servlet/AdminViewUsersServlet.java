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

        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // ✅ MODIFIED: Fetch two separate lists from the DAO
            List<User> donorList = UserDAO.getAllDonors();
            List<User> patientList = UserDAO.getAllPatients();

            // Set both lists as attributes for the JSP
            request.setAttribute("donors", donorList);
            request.setAttribute("patients", patientList);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/adminUsers.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load users at this time.");
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/adminUsers.jsp");
            rd.forward(request, response);
        }
    }
}