package servlet;

import dao.CommunityDAO;
import model.Comment;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/add-comment")
public class AddCommentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 1. Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // 2. Get the user and the form data
            User user = (User) session.getAttribute("user");
            int postId = Integer.parseInt(request.getParameter("postId"));
            String content = request.getParameter("commentContent");
            
            // 3. Create a new Comment object
            Comment newComment = new Comment();
            newComment.setPostId(postId);
            newComment.setUserId(user.getId());
            newComment.setCommentContent(content);
            
            // 4. Save the comment to the database
            CommunityDAO.addComment(newComment);
            
            // 5. Redirect back to the same post page (Post-Redirect-Get pattern)
            response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error adding new comment", e);
        }
    }
}