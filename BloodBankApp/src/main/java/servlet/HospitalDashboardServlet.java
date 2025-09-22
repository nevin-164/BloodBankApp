package servlet; // ⚠️ Make sure this package name matches yours!

import dao.DonationDAO;
import dao.RequestDAO;
import dao.StockDAO;
import dao.BloodInventoryDAO; // ✅ ADDED: New DAO for inventory
import model.Hospital;
import model.BloodInventory; // ✅ ADDED: New model for inventory

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import model.Request;
import model.Donation;

@WebServlet("/hospital-dashboard") // This is the new URL for the dashboard
public class HospitalDashboardServlet extends HttpServlet {

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
            // 2. Load ALL data
            int hospitalId = hospital.getId();
            
            // Original Data
            Map<String, Integer> currentStock = StockDAO.getStockByHospital(hospitalId);
            List<Request> pendingRequests = RequestDAO.getPendingRequestsForHospital(hospitalId);
            List<Donation> pendingDonations = DonationDAO.getPendingDonations(hospitalId);
            
            // Analytics Data (Feature 2)
            Map<String, Double> avgDonations = DonationDAO.getAverageDailyDonations(hospitalId);
            Map<String, Double> avgRequests = RequestDAO.getAverageDailyRequests(hospitalId);
            
            // Create a combined set of all blood groups
            Set<String> allBloodGroups = new HashSet<>(avgDonations.keySet());
            allBloodGroups.addAll(avgRequests.keySet());
            
            // ✅ NEW: Data for "Pending Inventory" Panel (Phase 4)
            List<BloodInventory> pendingBags = BloodInventoryDAO.getPendingBagsByHospital(hospitalId);

            // 3. Set all data as REQUEST attributes for the JSP
            request.setAttribute("currentStock", currentStock);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("pendingDonations", pendingDonations);
            request.setAttribute("avgDonations", avgDonations);
            request.setAttribute("avgRequests", avgRequests);
            request.setAttribute("allBloodGroups", allBloodGroups);
            request.setAttribute("pendingBags", pendingBags); // ✅ ADDED: New attribute
            request.setAttribute("hospital", hospital); 

            // 4. Forward to the JSP (the "View")
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while loading dashboard data.");
        }
    }
}