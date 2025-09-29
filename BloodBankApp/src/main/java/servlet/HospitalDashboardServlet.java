package servlet;

import dao.*;
import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the final, definitive version of the dashboard servlet.
 * Its primary responsibility is to fetch ALL necessary data for the hospital dashboard
 * and forward it to the JSP page. This version assumes all DAO calls will succeed.
 */
@WebServlet("/hospital-dashboard")
public class HospitalDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // 1. Security Check: Ensure a hospital is logged in.
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;
        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            int hospitalId = hospital.getId();

            // 2. Fetch All Data Models for the Dashboard
            Map<String, Integer> currentStock = StockDAO.getStockByHospital(hospitalId);
            List<Request> pendingRequests = RequestDAO.getPendingRequestsForHospital(hospitalId);
            List<Donation> pendingDonations = DonationDAO.getPendingDonations(hospitalId);
            List<BloodInventory> pendingBags = BloodInventoryDAO.getPendingBagsByHospital(hospitalId);
            
            List<Hospital> allHospitals = HospitalDAO.getAllHospitals();
            List<Hospital> otherHospitals = allHospitals.stream()
                .filter(h -> h.getId() != hospitalId)
                .collect(Collectors.toList());
            
            List<StockTransfer> pendingTransfers = StockTransferDAO.getPendingTransfersForHospital(hospitalId);
            List<BloodInventory> inTransitBags = BloodInventoryDAO.getInTransitBagsByHospital(hospitalId);

            // 3. Set All Fetched Data as Request Attributes for the JSP
            request.setAttribute("hospital", hospital);
            request.setAttribute("currentStock", currentStock);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("pendingDonations", pendingDonations);
            request.setAttribute("pendingBags", pendingBags);
            request.setAttribute("otherHospitals", otherHospitals);
            request.setAttribute("pendingTransfers", pendingTransfers);
            request.setAttribute("inTransitBags", inTransitBags);

            // 4. Forward the request and response to the JSP view.
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            // If any error occurs during data fetching, log it and show a clear error on the page.
            e.printStackTrace();
            request.setAttribute("errorMessage", "A critical error occurred while loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);
        }
    }
}

