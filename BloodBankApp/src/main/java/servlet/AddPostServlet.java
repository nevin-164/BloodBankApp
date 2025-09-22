package servlet;

import dao.CommunityDAO;
import model.Post;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/add-post")
public class AddPostServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 1. Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // 2. Get form data and user data
            User user = (User) session.getAttribute("user");
            String title = request.getParameter("postTitle");
            String content = request.getParameter("postContent");
            
            // 3. Create a new Post object
            Post newPost = new Post();
            newPost.setUserId(user.getId());
            newPost.setPostTitle(title);
            newPost.setPostContent(content);
            
            // 4. Save the post to the database
            CommunityDAO.addPost(newPost);
            
            // 5. Redirect back to the community page (Post-Redirect-Get pattern)
            response.sendRedirect(request.getContextPath() + "/community");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error adding new post", e);
        }
    }
}