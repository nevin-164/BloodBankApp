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
 * ✅ FINAL VERSION: This servlet acts as the central controller for the hospital dashboard.
 * Its sole responsibility is to gather all necessary data from various DAO classes
 * and forward it to the JSP for rendering. This corrected version fetches all pending requests
 * system-wide, which is consistent with the application's logic.
 */
@WebServlet("/hospital-dashboard")
public class HospitalDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // 1. Security Check: Ensure a hospital user is logged in.
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;
        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            int hospitalId = hospital.getId();

            // 2. Fetch All Data Models Required for the Dashboard View
            
            // --- Core Data ---
            Map<String, Integer> currentStock = StockDAO.getStockByHospital(hospitalId);
            List<Donation> pendingDonations = DonationDAO.getPendingDonations(hospitalId);
            
            // ✅ FIXED: Fetches ALL pending requests from the system, as requests are not tied to a single hospital.
            List<Request> pendingRequests = RequestDAO.getAllPendingRequests();

            // --- Detailed Inventory & Transfer Data ---
            List<BloodInventory> pendingBags = BloodInventoryDAO.getPendingBagsByHospital(hospitalId);
            List<StockTransfer> pendingTransfers = StockTransferDAO.getPendingTransfersForHospital(hospitalId);
            List<BloodInventory> inTransitBags = BloodInventoryDAO.getInTransitBagsByHospital(hospitalId);

            // --- Data for Transfer Forms ---
            List<Hospital> allHospitals = HospitalDAO.getAllHospitals();
            List<Hospital> otherHospitals = allHospitals.stream()
                    .filter(h -> h.getId() != hospitalId)
                    .collect(Collectors.toList());

            // 3. Set All Fetched Data as Request Attributes for the JSP
            request.setAttribute("hospital", hospital);
            request.setAttribute("currentStock", currentStock);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("pendingDonations", pendingDonations);
            request.setAttribute("pendingBags", pendingBags);
            request.setAttribute("otherHospitals", otherHospitals);
            request.setAttribute("pendingTransfers", pendingTransfers);
            request.setAttribute("inTransitBags", inTransitBags);

            // 4. Forward the request to the JSP view for rendering.
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            // If any error occurs during data fetching, show a clear error on the page.
            e.printStackTrace(); // Log the error for the developer
            request.setAttribute("errorMessage", "A critical error occurred while loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);
        }
    }
}