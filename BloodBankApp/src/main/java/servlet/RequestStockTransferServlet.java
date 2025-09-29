package servlet;

import dao.StockTransferDAO;
import model.Hospital;
import model.StockTransfer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/request-transfer")
public class RequestStockTransferServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        if (hospital == null) {
            response.sendRedirect("hospital-login.jsp");
            return;
        }

        try {
            int supplyingHospitalId = Integer.parseInt(request.getParameter("supplyingHospitalId"));
            String bloodGroup = request.getParameter("bloodGroup");
            int units = Integer.parseInt(request.getParameter("units"));

            StockTransfer transfer = new StockTransfer();
            transfer.setRequestingHospitalId(hospital.getId());
            transfer.setSupplyingHospitalId(supplyingHospitalId);
            transfer.setBloodGroup(bloodGroup);
            transfer.setUnits(units);

            StockTransferDAO.createTransferRequest(transfer);

            session.setAttribute("successMessage", "Stock transfer request submitted successfully!");
            response.sendRedirect("hospital-dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error submitting transfer request: " + e.getMessage());
            response.sendRedirect("hospital-dashboard");
        }
    }
}