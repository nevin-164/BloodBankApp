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
 * It now correctly and safely formats the emergency contacts list as a JSON string
 * to prevent any special characters from breaking the web page's JavaScript.
 */
@WebServlet("/hospital-dashboard")
public class HospitalDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);

        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;
        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        try {
            int hospitalId = hospital.getId();

            // --- 1. Fetch Core Dashboard Data (Unchanged) ---
            List<Request> pendingRequests = RequestDAO.getAllPendingRequests(hospitalId);
            List<Donation> pendingDonations = DonationDAO.getActionableDonationsForHospital(hospitalId);
            Map<String, Integer> currentStock = StockDAO.getStockByHospital(hospitalId);
            List<BloodInventory> pendingBags = BloodInventoryDAO.getPendingBagsByHospital(hospitalId);
            List<StockTransfer> pendingTransfers = StockTransferDAO.getPendingTransfersForHospital(hospitalId);
            List<BloodInventory> inTransitBags = BloodInventoryDAO.getInTransitBagsByHospital(hospitalId);
            List<Hospital> allHospitals = HospitalDAO.getAllHospitals();
            List<Hospital> otherHospitals = allHospitals.stream()
                    .filter(h -> h.getId() != hospitalId)
                    .collect(Collectors.toList());

            // --- 2. Emergency Contact Logic (Unchanged) ---
            List<User> allActiveEmergencyDonors = EmergencyDonorDAO.getActiveEmergencyDonors();
            Map<String, List<User>> emergencyDonorsByGroup = allActiveEmergencyDonors.stream()
                .collect(Collectors.groupingBy(User::getBloodGroup));
            
            Map<String, List<User>> emergencyContacts = new HashMap<>();
            String[] allBloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            
            for (String bloodGroup : allBloodGroups) {
                if (currentStock.getOrDefault(bloodGroup, 0) == 0) {
                    List<User> donors = emergencyDonorsByGroup.get(bloodGroup);
                    if (donors != null && !donors.isEmpty()) {
                        emergencyContacts.put(bloodGroup, donors);
                    }
                }
            }

            // --- 3. ✅ CRITICAL FIX: Convert emergency contacts to a safe JSON string ---
            String emergencyContactsJson = convertMapToJson(emergencyContacts);
            request.setAttribute("emergencyContactsJson", emergencyContactsJson);

            // --- 4. Set Attributes for JSP ---
            request.setAttribute("hospital", hospital);
            request.setAttribute("currentStock", currentStock);
            request.setAttribute("pendingDonations", pendingDonations);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("pendingBags", pendingBags);
            request.setAttribute("otherHospitals", otherHospitals);
            request.setAttribute("pendingTransfers", pendingTransfers);
            request.setAttribute("inTransitBags", inTransitBags);

            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "A critical error occurred while loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);
        }
    }

    /**
     * ✅ NEW & CRITICAL HELPER: Manually builds a JSON object from the map.
     * This safely handles special characters in names, which was the root cause of the problem.
     */
    private String convertMapToJson(Map<String, List<User>> map) {
        StringBuilder json = new StringBuilder("{");
        boolean firstGroup = true;
        for (Map.Entry<String, List<User>> entry : map.entrySet()) {
            if (!firstGroup) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":[");
            boolean firstDonor = true;
            for (User donor : entry.getValue()) {
                if (!firstDonor) {
                    json.append(",");
                }
                json.append("{\"id\":").append(donor.getId()).append(",\"name\":\"").append(escapeJson(donor.getName())).append("\"}");
                firstDonor = false;
            }
            json.append("]");
            firstGroup = false;
        }
        json.append("}");
        return json.toString();
    }

    /**
     * ✅ NEW & CRITICAL HELPER: Escapes characters for safe inclusion in a JSON string.
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}