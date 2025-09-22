package servlet;

import dao.StockDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder; // ✅ ADDED

@WebServlet("/admin/update-stock")
public class AdminUpdateStockServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // Security Check for Admin role
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String hospitalIdParam = req.getParameter("hospital_id");
        String successMessage = "";
        String errorMessage = "";
        
        try {
            int hospitalId = Integer.parseInt(hospitalIdParam);
            String bloodGroup = req.getParameter("blood_group");
            int units = Integer.parseInt(req.getParameter("units"));

            // ✅ MODIFIED: This call now WORKS because our hybrid DAO has the setStock method.
            StockDAO.setStock(hospitalId, bloodGroup, units);
            successMessage = "Stock updated successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Update failed.";
        }
        
        // This redirect is fine, assuming /stock.jsp is your admin stock page
        String redirectURL = req.getContextPath() + "/stock.jsp?hospital_id=" + hospitalIdParam;
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "&success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "&error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
}