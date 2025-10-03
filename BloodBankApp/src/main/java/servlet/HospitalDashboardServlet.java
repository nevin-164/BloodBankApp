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
 * ✅ DEFINITIVE FINAL VERSION: This servlet provides data for the dashboard.
 * It now implements a highly targeted emergency donor list, showing donors
 * ONLY for the specific blood groups that are either at zero stock OR have
 * insufficient stock to fulfill a pending patient request.
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

            // 1. Fetch all data required for the dashboard
            Map<String, Integer> currentStock = StockDAO.getStockByHospital(hospitalId);
            List<Hospital> otherHospitals = HospitalDAO.getAllHospitalsExcept(hospitalId);
            List<StockTransfer> pendingTransfers = StockTransferDAO.getPendingTransfersForHospital(hospitalId);
            List<BloodInventory> inTransitBags = BloodInventoryDAO.getInTransitBagsByHospital(hospitalId);
            List<Request> pendingRequests = RequestDAO.getAllPendingRequests(hospitalId);
            List<Donation> pendingDonations = DonationDAO.getActionableDonationsForHospital(hospitalId);
            List<BloodInventory> pendingBags = BloodInventoryDAO.getPendingBagsByHospital(hospitalId);
            
            // 2. ✅ NEW LOGIC: Prepare a targeted list of emergency contacts.
            // First, get all active donors and group them by blood type for easy lookup.
            List<User> allActiveEmergencyDonors = EmergencyDonorDAO.getActiveEmergencyDonors();
            Map<String, List<User>> emergencyDonorsByGroup = allActiveEmergencyDonors.stream()
                .filter(donor -> donor.getBloodGroup() != null && !donor.getBloodGroup().trim().isEmpty())
                .collect(Collectors.groupingBy(User::getBloodGroup));
            
            // Create the final map that will be sent to the page.
            Map<String, List<User>> emergencyContacts = new HashMap<>();
            String[] allBloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

            // Condition 1: Add donors for any blood group with ZERO stock.
            for (String bloodGroup : allBloodGroups) {
                if (currentStock.getOrDefault(bloodGroup, 0) == 0) {
                    if (emergencyDonorsByGroup.containsKey(bloodGroup)) {
                        emergencyContacts.put(bloodGroup, emergencyDonorsByGroup.get(bloodGroup));
                    }
                }
            }
            
            // Condition 2: Add donors for any pending request where stock is insufficient.
            for (Request req : pendingRequests) {
                String bloodGroup = req.getBloodGroup();
                int stockForRequest = currentStock.getOrDefault(bloodGroup, 0);
                
                // If requested units are more than available stock AND this group isn't already listed
                if (req.getUnits() > stockForRequest && !emergencyContacts.containsKey(bloodGroup)) {
                    if (emergencyDonorsByGroup.containsKey(bloodGroup)) {
                        emergencyContacts.put(bloodGroup, emergencyDonorsByGroup.get(bloodGroup));
                    }
                }
            }

            // 3. Set all attributes for the JSP
            request.setAttribute("hospital", hospital);
            request.setAttribute("currentStock", currentStock);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("pendingDonations", pendingDonations);
            request.setAttribute("pendingBags", pendingBags);
            request.setAttribute("otherHospitals", otherHospitals);
            request.setAttribute("pendingTransfers", pendingTransfers);
            request.setAttribute("inTransitBags", inTransitBags);
            
            request.setAttribute("emergencyContactsJson", convertMapToJson(emergencyContacts));
            request.setAttribute("emergencyContacts", emergencyContacts);

            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "A critical error occurred while loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/hospital-dashboard.jsp").forward(request, response);
        }
    }

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
                json.append("{\"id\":").append(donor.getId())
                    .append(",\"name\":\"").append(escapeJson(donor.getName()))
                    .append("\",\"contactNumber\":\"").append(escapeJson(donor.getContactNumber()))
                    .append("\"}");
                firstDonor = false;
            }
            json.append("]");
            firstGroup = false;
        }
        json.append("}");
        return json.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("'", "\\'")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}