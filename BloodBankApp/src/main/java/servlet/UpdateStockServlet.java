package servlet;

import dao.StockDAO;
import model.Hospital; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder; // ✅ ADDED

@WebServlet("/update-stock")
public class UpdateStockServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;
        
        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";
        String redirectURL = request.getContextPath() + "/hospital-dashboard"; // ✅ FIX: Point to the servlet

        try {
            String bloodGroup = request.getParameter("bloodGroup");
            int units = Integer.parseInt(request.getParameter("units"));

            StockDAO.addUnits(hospital.getId(), bloodGroup, units);
            
            successMessage = "Stock updated successfully!";

        } catch (NumberFormatException e) {
            errorMessage = "Invalid number of units.";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "An error occurred.";
        }

        // Add the success/error message and redirect
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        response.sendRedirect(redirectURL);
    }
}