package servlet;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/donor-list") // We'll put all new admin pages under /admin/
public class AdminDonorListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 1. Security Check: Must be logged in AND be an ADMIN
        if (session == null || session.getAttribute("user") == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // 2. Get all donors from the DAO
            List<User> donorList = UserDAO.getAllDonors();
            
            // 3. Set the list as an attribute for the JSP
            request.setAttribute("donorList", donorList);
            
            // 4. Forward to the new JSP we are about to create
            request.getRequestDispatcher("/admin/donor-list.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error loading donor list", e);
        }
    }
}