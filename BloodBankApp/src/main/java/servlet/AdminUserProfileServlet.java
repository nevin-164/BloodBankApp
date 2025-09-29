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

/**
 * This is the final, corrected version of the AdminUserProfileServlet.
 * It is now fully synchronized with the definitive RequestDAO.
 */
@WebServlet("/admin/user-profile")
public class AdminUserProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 1. Security Check: Must be logged in AND be an ADMIN
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedInUser == null || !"ADMIN".equals(loggedInUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // 2. Get the user ID from the URL
            int userId = Integer.parseInt(request.getParameter("id"));
            
            // 3. Fetch ALL data for this user from all DAOs
            User profileUser = UserDAO.getUserById(userId);
            List<Donation> donationHistory = DonationDAO.getDonationsByUserId(userId);
            
            // ✅ FIXED: Using the single, correct method name from the final DAO.
            List<Request> requestHistory = RequestDAO.getRequestsByUserId(userId);
            
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

