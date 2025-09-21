package servlet;

import dao.DonationDAO;
import dao.StockDAO;
import dao.UserDAO;
import dao.AchievementDAO;
import model.Donation;

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

            if (donation != null) {
                // 1. Add stock to this hospital
                StockDAO.addUnits(donation.getHospitalId(), donation.getBloodGroup(), donation.getUnits());
                
                // 2. Mark the donation as approved
                DonationDAO.updateDonationStatus(donationId, "APPROVED");

                // 3. Update the donor's eligibility dates
                LocalDate today = LocalDate.now();
                Date lastDonationDate = Date.valueOf(today);
                Date nextEligibleDate = Date.valueOf(today.plusDays(COOLING_DAYS));
                UserDAO.updateDonationDates(donation.getUserId(), lastDonationDate, nextEligibleDate);
                
                
                // --- ✅ Gamification Logic ---
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
                        
                        // ✅ FIX: Changed from >= 1 to >= 2
                        // This ensures it's only awarded on the 2nd (or more) donation in a year.
                        if (yearlyDonations >= 2) { 
                            AchievementDAO.addAchievement(userId, "Annual Donor", "images/badges/annual.png");
                        }
                    }

                } catch (Exception e_ach) {
                	System.err.println("Gamification (Achievement) Error: " + e_ach.getMessage());
                }
                // --- End Gamification Logic ---
                
                
                successMessage = "Donation approved and stock updated!";
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