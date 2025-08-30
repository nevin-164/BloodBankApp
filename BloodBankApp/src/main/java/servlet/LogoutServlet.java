package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); // Get session if it exists
        String redirectPage = request.getContextPath() + "/index.jsp"; // Default redirect for regular users

        if (session != null) {
            // ✅ MODIFICATION: Check if the logging-out user is a hospital
            // This check must happen BEFORE the session is invalidated.
            if (session.getAttribute("hospital") != null) {
                redirectPage = request.getContextPath() + "/hospital-login.jsp";
            }
            
            // Invalidate the session to log the user out
            session.invalidate();
        }
        
        // Redirect to the appropriate page
        response.sendRedirect(redirectPage);
    }
}