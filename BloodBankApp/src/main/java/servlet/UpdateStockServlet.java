package servlet;

import dao.StockDAO;
import model.Hospital; // ✅ ADDED: Import the Hospital model
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/update-stock")
public class UpdateStockServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // ✅ MODIFIED: Get the full Hospital object from the session
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;
        
        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            String bloodGroup = request.getParameter("bloodGroup");
            int units = Integer.parseInt(request.getParameter("units"));

            // ✅ FIXED: Pass the logged-in hospital's ID to the DAO method
            StockDAO.addUnits(hospital.getId(), bloodGroup, units);
            
            response.sendRedirect(request.getContextPath() + "/hospital-dashboard.jsp?success=Stock+updated+successfully!");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/hospital-dashboard.jsp?error=Invalid+number+of+units.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/hospital-dashboard.jsp?error=An+error+occurred.");
        }
    }
}