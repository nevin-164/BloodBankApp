package servlet;

import dao.DonationDAO;
import dao.StockDAO;
import dao.UserDAO;
import dao.AchievementDAO;
import dao.BloodInventoryDAO; // ✅ ADDED: New inventory DAO
import model.Donation;
import model.BloodInventory; // ✅ ADDED: New inventory model

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
import java.time.LocalDate;

@WebServlet("/approve-donation")
public class ApproveDonationServlet extends HttpServlet {
    private static final int COOLING_DAYS = 90;
    private static final int BLOOD_EXPIRY_DAYS = 42; // Standard expiry for RBCs

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("hospital") == null) {
            res.sendRedirect(req.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String successMessage = "";
        String errorMessage = "";
        int donationId = 0; 

        try {
            donationId = Integer.parseInt(req.getParameter("donationId"));
            Donation donation = DonationDAO.getDonationById(donationId);
            LocalDate today = LocalDate.now();

            if (donation != null) {
                
                // --- ✅ MODIFIED: Advanced Inventory Logic (Phase 4) ---
                // We no longer call StockDAO.addUnits().
                // Instead, we create a new, trackable bag for each unit.
                
                // 1. Calculate donation and expiry dates
                Date dateDonated = Date.valueOf(today);
                Date expiryDate = Date.valueOf(today.plusDays(BLOOD_EXPIRY_DAYS));
                
                // 2. Save a new bag record for EACH unit donated
                for (int i = 0; i < donation.getUnits(); i++) {
                    BloodInventory newBag = new BloodInventory();
                    newBag.setDonationId(donationId);
                    newBag.setHospitalId(donation.getHospitalId());
                    newBag.setBloodGroup(donation.getBloodGroup());
                    newBag.setDateDonated(dateDonated);
                    newBag.setExpiryDate(expiryDate);
                    newBag.setInventoryStatus("PENDING_TEST"); // Set initial status
                    
                    BloodInventoryDAO.addBag(newBag); 
                }
                // --- End of New Logic ---

                
                // 3. Mark the donation as approved
                DonationDAO.updateDonationStatus(donationId, "APPROVED");

                // 4. Update the donor's eligibility dates
                Date lastDonationDate = Date.valueOf(today);
                Date nextEligibleDate = Date.valueOf(today.plusDays(COOLING_DAYS));
                UserDAO.updateDonationDates(donation.getUserId(), lastDonationDate, nextEligibleDate);
                
                
                // --- Gamification Logic (Still works!) ---
                try {
                    int userId = donation.getUserId();
                    int totalDonations = 0; 
                    
                    // --- Check 1: First Donation ---
                    boolean hasFirstBadge = AchievementDAO.hasAchievement(userId, "First Donation");
                    if (!hasFirstBadge) {
                        totalDonations = DonationDAO.getDonationCountForUser(userId);
                        if (totalDonations == 1) { // This was their first one
                            AchievementDAO.addAchievement(userId, "First Donation", "images/badges/first-donation.png");
                        }
                    }
                    
                    // --- Check 2: 5-Time Donor ---
                    boolean has5Badge = AchievementDAO.hasAchievement(userId, "5-Time Donor");
                    if (!has5Badge) {
                        if (totalDonations == 0) totalDonations = DonationDAO.getDonationCountForUser(userId);
                        if (totalDonations >= 5) {
                            AchievementDAO.addAchievement(userId, "5-Time Donor", "images/badges/5-time.png");
                        }
                    }
                    
                    // --- Check 3: 10-Time Donor ---
                    boolean has10Badge = AchievementDAO.hasAchievement(userId, "10-Time Donor");
                    if (!has10Badge) {
                        if (totalDonations == 0) totalDonations = DonationDAO.getDonationCountForUser(userId);
                        if (totalDonations >= 10) {
                            AchievementDAO.addAchievement(userId, "10-Time Donor", "images/badges/10-time.png");
                        }
                    }
                    
                    // --- Check 4: Annual Donor ---
                    boolean hasAnnualBadge = AchievementDAO.hasAchievement(userId, "Annual Donor");
                    if (!hasAnnualBadge) {
                        int yearlyDonations = DonationDAO.getDonationCountInPastYear(userId);
                        if (yearlyDonations >= 2) { 
                            AchievementDAO.addAchievement(userId, "Annual Donor", "images/badges/annual.png");
                        }
                    }

                } catch (Exception e_ach) {
                	System.err.println("Gamification (Achievement) Error: " + e_ach.getMessage());
                }
                // --- End Gamification Logic ---
                
                
                successMessage = "Donation approved and inventory updated!";
            } else {
                errorMessage = "Donation not found.";
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid Donation ID.";
        } catch (Exception e) {
            errorMessage = "Error approving donation.";
            e.printStackTrace(); 
        }

        // Redirect to the dashboard
        String redirectURL = req.getContextPath() + "/hospital-dashboard";
        
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        res.sendRedirect(redirectURL);
    }
}