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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ✅ FINAL VERSION: This servlet acts as the central controller for the hospital dashboard.
 * It has been updated to fetch all actionable donations AND to find available
 * emergency donors when a blood type is out of stock.
 */
@WebServlet("/hospital-dashboard")
public class HospitalDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // 1. Security Check
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;
        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            int hospitalId = hospital.getId();

            // 2. Fetch All Standard Data Models
            List<Donation> pendingDonations = DonationDAO.getActionableDonationsForHospital(hospitalId);
            Map<String, Integer> currentStock = StockDAO.getStockByHospital(hospitalId);
            List<Request> pendingRequests = RequestDAO.getAllPendingRequests();
            List<BloodInventory> pendingBags = BloodInventoryDAO.getPendingBagsByHospital(hospitalId);
            List<StockTransfer> pendingTransfers = StockTransferDAO.getPendingTransfersForHospital(hospitalId);
            List<BloodInventory> inTransitBags = BloodInventoryDAO.getInTransitBagsByHospital(hospitalId);
            
            List<Hospital> allHospitals = HospitalDAO.getAllHospitals();
            List<Hospital> otherHospitals = allHospitals.stream()
                    .filter(h -> h.getId() != hospitalId)
                    .collect(Collectors.toList());

            // --- ✅ FINAL FIX: Find Emergency Contacts for Out-of-Stock Blood Types ---
            Map<String, List<User>> emergencyContacts = new HashMap<>();
            String[] allBloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            
            for (String bloodGroup : allBloodGroups) {
                // Check if stock for this blood group is 0 or not present in the map
                if (currentStock.getOrDefault(bloodGroup, 0) == 0) {
                    // If stock is zero, find available emergency donors for that group.
                    List<User> donors = EmergencyDonorDAO.getAvailableEmergencyDonors(bloodGroup);
                    if (donors != null && !donors.isEmpty()) {
                        emergencyContacts.put(bloodGroup, donors);
                    }
                }
            }

            // 3. Set All Data as Request Attributes for the JSP
            request.setAttribute("hospital", hospital);
            request.setAttribute("currentStock", currentStock);
            request.setAttribute("pendingDonations", pendingDonations);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("pendingBags", pendingBags);
            request.setAttribute("otherHospitals", otherHospitals);
            request.setAttribute("pendingTransfers", pendingTransfers);
            request.setAttribute("inTransitBags", inTransitBags);
            request.setAttribute("emergencyContacts", emergencyContacts); // Add the new list

            // 4. Forward to the JSP view
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "A critical error occurred while loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);
        }
    }
}