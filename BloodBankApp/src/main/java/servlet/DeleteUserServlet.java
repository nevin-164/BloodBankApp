package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * ✅ FINAL VERSION: Handles the deletion of a user.
 * This servlet now calls the safe, transactional method `deleteUserAndAnonymizeData`
 * from the UserDAO. This ensures that when a user is deleted, their historical
 * donation and request data is preserved anonymously, protecting the integrity
 * of the hospital's stock records.
 */
@WebServlet("/delete-user")
public class DeleteUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String successMessage = "";
        String errorMessage = "";

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            
            // ✅ CRITICAL FIX: Call the new, safe method that anonymizes data before deleting.
            UserDAO.deleteUserAndAnonymizeData(userId);
            
            successMessage = "User #" + userId + " has been successfully deleted, and their historical donation records have been preserved.";

        } catch (NumberFormatException e) {
            errorMessage = "Invalid user ID provided.";
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage = "An error occurred during the deletion process: " + e.getMessage();
            e.printStackTrace();
        }

        // Redirect back to the user list with a status message.
        String redirectURL = request.getContextPath() + "/admin/users";
        if (!successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (!errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        response.sendRedirect(redirectURL);
    }
}