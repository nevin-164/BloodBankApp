package servlet;

import dao.UserDAO;
import dao.AchievementDAO; // ✅ ADDED: Import our new DAO
import model.User; // ✅ ADDED: Import the User model
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        String bloodGroup = req.getParameter("blood_group");
        String contactNumber = req.getParameter("contact_number");

        try {
            // Server-side validation
            if (!"DONOR".equals(role) && !"PATIENT".equals(role)) {
                req.setAttribute("msg", "Invalid role selected.");
                req.getRequestDispatcher("register.jsp").forward(req, res);
                return;
            }
            
            if (UserDAO.isEmailExists(email)) {
                req.setAttribute("msg", "An account with this email already exists.");
                req.getRequestDispatcher("register.jsp").forward(req, res);
                return;
            }
            
            if ("DONOR".equals(role) && (bloodGroup == null || bloodGroup.isEmpty())) {
                 req.setAttribute("msg", "Donors must provide a blood group.");
                 req.getRequestDispatcher("register.jsp").forward(req, res);
                 return;
            }

            // 1. Create the user
            UserDAO.insert(name, email, password, role, bloodGroup, contactNumber);
            
            
            // --- ✅ NEW: Gamification Logic for Blood Type ---
            if ("DONOR".equals(role)) {
                try {
                    // 2. Get the new user we just created to get their ID
                    User newUser = UserDAO.findByEmailAndPassword(email, password); 
                    
                    if (newUser != null) {
                        String bg = newUser.getBloodGroup();
                        
                        // 3. Check blood type and award badge
                        if ("O-".equals(bg)) {
                            AchievementDAO.addAchievement(newUser.getId(), 
                                                          "Universal Donor", 
                                                          "images/badges/universal-donor.png");
                        } else if ("AB+".equals(bg)) {
                            AchievementDAO.addAchievement(newUser.getId(), 
                                                          "Universal Recipient", 
                                                          "images/badges/universal-recipient.png");
                        }
                    }
                } catch (Exception e_ach) {
                    // If gamification fails, don't stop the registration.
                    System.err.println("Gamification (Blood Type) Error: " + e_ach.getMessage());
                }
            }
            // --- End Gamification Logic ---
            

            // 4. Redirect to login
            res.sendRedirect("login.jsp?success=Registration+successful.+Please+login.");

        } catch (Exception e) {
            throw new ServletException("Error during user registration.", e);
        }
    }
}