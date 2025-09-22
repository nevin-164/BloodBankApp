package servlet;

import dao.CommunityDAO;
import model.Post;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/community")
public class CommunityServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // 1. Get all posts from the DAO
            List<Post> postList = CommunityDAO.getAllPosts();
            
            // 2. Set the list as an attribute to be used by the JSP
            request.setAttribute("postList", postList);
            
            // 3. Forward to the JSP page
            request.getRequestDispatcher("/community.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            // You can forward to an error page or send an error
            throw new ServletException("Error loading community page", e);
        }
    }
}