package servlet;

import dao.UserDAO;
import dao.DonationDAO;
import dao.RequestDAO;
import dao.AchievementDAO;
import model.User;
import model.Donation;
import model.Request;
import model.Achievement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/user-profile") // Matches the link from the donor-list.jsp
public class AdminUserProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 1. Security Check: Must be logged in AND be an ADMIN
        if (session == null || session.getAttribute("user") == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // 2. Get the user ID from the URL
            int userId = Integer.parseInt(request.getParameter("id"));
            
            // 3. Fetch ALL data for this user from all DAOs
            User profileUser = UserDAO.getUserById(userId);
            List<Donation> donationHistory = DonationDAO.getDonationsByUserId(userId);
            List<Request> requestHistory = RequestDAO.getRequestsByPatientId(userId);
            List<Achievement> achievements = AchievementDAO.getAchievementsForUser(userId);
            
            // 4. Set all data as request attributes for the JSP
            request.setAttribute("profileUser", profileUser);
            request.setAttribute("donationHistory", donationHistory);
            request.setAttribute("requestHistory", requestHistory);
            request.setAttribute("achievements", achievements);
            
            // 5. Forward to the new JSP
            request.getRequestDispatcher("/admin/user-profile.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error loading user profile", e);
        }
    }
}