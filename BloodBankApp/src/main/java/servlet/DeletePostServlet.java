package servlet;

import dao.CommunityDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/delete-post")
public class DeletePostServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 1. Security Check: Must be logged in AND be an ADMIN
        if (session == null || session.getAttribute("user") == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // 2. Get the Post ID from the URL
            int postId = Integer.parseInt(request.getParameter("postId"));
            
            // 3. Call the DAO to delete the post and its comments
            CommunityDAO.deletePost(postId);
            
            // 4. Redirect back to the main community page
            response.sendRedirect(request.getContextPath() + "/community");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error deleting post", e);
        }
    }
}