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

@WebServlet("/admin/update-stock")
public class AdminUpdateStockServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // Security Check for Admin role
        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int hospitalId = Integer.parseInt(req.getParameter("hospital_id"));
            String bloodGroup = req.getParameter("blood_group");
            int units = Integer.parseInt(req.getParameter("units"));

            // Use the 'setStock' method to manually override the stock level
            StockDAO.setStock(hospitalId, bloodGroup, units);

            // Redirect back to the stock page for the same hospital with a success message
            res.sendRedirect(req.getContextPath() + "/stock.jsp?hospital_id=" + hospitalId + "&success=Stock+updated+successfully!");

        } catch (Exception e) {
            String hospitalId = req.getParameter("hospital_id");
            // Redirect back with an error message
            res.sendRedirect(req.getContextPath() + "/stock.jsp?hospital_id=" + hospitalId + "&error=Update+failed.");
        }
    }
}