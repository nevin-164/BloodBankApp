package servlet;

import dao.EmergencyDonorDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/emergency-signup")
public class EmergencySignupServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User donor = (User) session.getAttribute("user");

        try {
            EmergencyDonorDAO.signUp(donor.getId());
            res.sendRedirect(req.getContextPath() + "/donate?success=You+are+now+an+emergency+donor+for+one+week!");
        } catch (Exception e) {
            throw new ServletException("Error signing up for emergency donation", e);
        }
    }
}