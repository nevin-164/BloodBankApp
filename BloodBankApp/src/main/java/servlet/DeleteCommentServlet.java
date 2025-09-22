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

@WebServlet("/delete-comment")
public class DeleteCommentServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 1. Security Check: Must be logged in AND be an ADMIN
        if (session == null || session.getAttribute("user") == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // 2. Get both IDs from the URL
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            int postId = Integer.parseInt(request.getParameter("postId")); // Needed for redirect
            
            // 3. Call the DAO to delete the comment
            CommunityDAO.deleteComment(commentId);
            
            // 4. Redirect back to the post page
            response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error deleting comment", e);
        }
    }
}