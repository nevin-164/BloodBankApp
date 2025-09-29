package servlet;

import dao.BloodInventoryDAO;
import model.Hospital;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/manual-add-stock")
public class ManualAddStockServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        if (hospital == null) {
            response.sendRedirect("hospital-login.jsp");
            return;
        }

        try {
            String bloodGroup = request.getParameter("bloodGroup");
            int units = Integer.parseInt(request.getParameter("units"));

            if (units <= 0) {
                 session.setAttribute("errorMessage", "Number of units must be positive.");
                 response.sendRedirect("hospital-dashboard");
                 return;
            }

            // Loop to create a distinct record for each bag
            for (int i = 0; i < units; i++) {
                BloodInventoryDAO.manuallyAddClearedBag(hospital.getId(), bloodGroup);
            }

            session.setAttribute("successMessage", units + " units of " + bloodGroup + " blood have been successfully added to your cleared stock.");
            response.sendRedirect("hospital-dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error adding stock: " + e.getMessage());
            response.sendRedirect("hospital-dashboard");
        }
    }
}
