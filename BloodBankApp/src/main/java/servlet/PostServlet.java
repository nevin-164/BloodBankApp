package servlet;

import dao.CommunityDAO;
import model.Post;
import model.Comment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/post")
public class PostServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // 1. Get the post ID from the URL parameter
            int postId = Integer.parseInt(request.getParameter("id"));
            
            // 2. Fetch the main post from the DAO
            Post post = CommunityDAO.getPostById(postId);
            
            // 3. Fetch all comments for that post
            List<Comment> comments = CommunityDAO.getCommentsForPost(postId);
            
            // 4. Set them as attributes for the JSP
            request.setAttribute("post", post);
            request.setAttribute("comments", comments);
            
            // 5. Forward to the post.jsp page
            request.getRequestDispatcher("/post.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error loading post", e);
        }
    }
}