package dao;

import model.Donation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing donations.
 * This version now correctly handles inventory creation, donor cooldowns,
 * emergency status updates, and achievement awards within a single, robust transaction.
 */
public class DonationDAO {

    // --- Private Helper Methods (For Internal Use Only) ---

    private static void updateDonationStatus(int donationId, String status, Connection con) throws SQLException {
        String sql = "UPDATE donations SET status = ? WHERE donation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, donationId);
            ps.executeUpdate();
        }
    }

    private static void setDonationCompletionDate(int donationId, java.sql.Date actualDonationDate, Connection con) throws SQLException {
        String sql = "UPDATE donations SET donation_date = ?, expiry_date = DATE_ADD(?, INTERVAL 42 DAY) WHERE donation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, actualDonationDate);
            ps.setDate(2, actualDonationDate);
            ps.setInt(3, donationId);
            ps.executeUpdate();
        }
    }

    private static void checkAndAwardAchievements(int userId, Connection con) throws SQLException {
        if (!AchievementDAO.hasAchievement(userId, "First Donation", con)) {
            if (getDonationCountForUser(userId, con) == 1) {
                AchievementDAO.addAchievement(userId, "First Donation", "images/badges/first-donation.png", con);
            }
        }
        if (!AchievementDAO.hasAchievement(userId, "Annual Donor", con)) {
            if (getDonationCountInPastYear(userId, con) >= 4) {
                AchievementDAO.addAchievement(userId, "Annual Donor", "images/badges/annual.png", con);
            }
        }
    }


    // --- Public Methods (Safe for Servlets to Call) ---

    public static void approveDonationTransaction(int donationId, Date dateDonated) throws Exception {
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false);

            Donation donation = getDonationById(donationId, con);
            if (donation == null) {
                throw new SQLException("Donation with ID " + donationId + " not found.");
            }
            int userId = donation.getUserId();
            int hospitalId = donation.getHospitalId();
            String bloodGroup = donation.getBloodGroup();
            int units = donation.getUnits();

            // 1. Set the completion date and update the donation status to 'APPROVED'
            setDonationCompletionDate(donationId, dateDonated, con);
            updateDonationStatus(donationId, "APPROVED", con);

            // 2. ✅ **CRITICAL FIX**: Add the donated units to the blood inventory as 'PENDING_TESTS'
            for (int i = 0; i < units; i++) {
                BloodInventoryDAO.addBag(donationId, hospitalId, bloodGroup, dateDonated, con);
            }

            // 3. Update the donor's eligibility for future donations
            Date nextEligibleDate = Date.valueOf(dateDonated.toLocalDate().plusDays(90));
            UserDAO.updateDonationDates(userId, dateDonated, nextEligibleDate, con);

            // 4. Check for and award any new achievements
            checkAndAwardAchievements(userId, con);

            // 5. Expire the donor's emergency volunteer status
            EmergencyDonorDAO.setDonorOnCooldown(userId);

            con.commit();

        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
            throw new SQLException("The donation approval transaction failed. Reason: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public static void approvePreScreeningTransaction(int donationId) throws SQLException {
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false);

            Donation donation = getDonationById(donationId, con);
            if (donation == null) {
                throw new SQLException("Donation with ID " + donationId + " not found.");
            }
            if (donation.getAppointmentDate() == null) {
                throw new SQLException("Appointment date is missing for donation ID " + donationId);
            }

            updateDonationStatus(donationId, "PRE-SCREEN_PASSED", con);

            con.commit();

        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
            throw new SQLException("Error in pre-screening approval transaction: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public static void declineDonation(int donationId) throws SQLException {
        String sql = "UPDATE donations SET status = 'DECLINED' WHERE donation_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            ps.executeUpdate();
        }
    }

    public static void clearDonationNotification(int donationId) throws SQLException {
        String sql = "UPDATE donations SET status = CASE " +
                     "WHEN status = 'APPROVED' THEN 'COMPLETED' " +
                     "WHEN status = 'DECLINED' THEN 'CLOSED' " +
                     "END " +
                     "WHERE donation_id = ? AND (status = 'APPROVED' OR status = 'DECLINED')";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            ps.executeUpdate();
        }
    }

    public static void updateDonationStatusForHospital(int donationId, String newStatus) throws SQLException {
        String sql = "UPDATE donations SET status = ? WHERE donation_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, donationId);
            ps.executeUpdate();
        }
    }

    public static void createDonationAppointment(int userId, int hospitalId, int units, java.sql.Date appointmentDate) throws Exception {
        String insertSql = "INSERT INTO donations (user_id, hospital_id, units, blood_group, status, appointment_date, donation_date) " +
                           "VALUES (?, ?, ?, (SELECT blood_group FROM users WHERE user_id = ?), 'PENDING', ?, CURDATE())";
        try (Connection con = DBUtil.getConnection(); PreparedStatement psInsert = con.prepareStatement(insertSql)) {
            psInsert.setInt(1, userId);
            psInsert.setInt(2, hospitalId);
            psInsert.setInt(3, units);
            psInsert.setInt(4, userId);
            psInsert.setDate(5, appointmentDate);
            psInsert.executeUpdate();
        }
    }

    public static Donation getPendingAppointmentForDonor(int userId) throws Exception {
        String sql = "SELECT d.appointment_date, h.name as hospital_name, d.status FROM donations d " +
                     "JOIN hospitals h ON d.hospital_id = h.hospital_id " +
                     "WHERE d.user_id = ? AND d.status IN ('PENDING', 'PRE-SCREEN_PASSED')";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Donation appointment = new Donation();
                    appointment.setAppointmentDate(rs.getDate("appointment_date"));
                    appointment.setHospitalName(rs.getString("hospital_name"));
                    appointment.setStatus(rs.getString("status"));
                    return appointment;
                }
            }
        }
        return null;
    }

    public static List<Donation> getPendingDonations(int hospitalId) throws Exception {
        List<Donation> appointments = new ArrayList<>();
        String sql = "SELECT d.donation_id, u.name as donor_name, d.blood_group, d.units, d.appointment_date, d.status " +
                     "FROM donations d JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.status = 'PENDING' AND d.hospital_id = ? " +
                     "ORDER BY d.appointment_date ASC";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donation appt = new Donation();
                    appt.setDonationId(rs.getInt("donation_id"));
                    appt.setDonorName(rs.getString("donor_name"));
                    appt.setBloodGroup(rs.getString("blood_group"));
                    appt.setUnits(rs.getInt("units"));
                    appt.setAppointmentDate(rs.getDate("appointment_date"));
                    appt.setStatus(rs.getString("status"));
                    appointments.add(appt);
                }
            }
        }
        return appointments;
    }

    public static List<Donation> getDonationsByUserId(int userId) throws SQLException {
        List<Donation> history = new ArrayList<>();
        String sql = "SELECT d.*, h.name as hospital_name FROM donations d " +
                     "LEFT JOIN hospitals h ON d.hospital_id = h.hospital_id " +
                     "WHERE d.user_id = ? ORDER BY d.appointment_date DESC";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donation donation = new Donation();
                    donation.setDonationId(rs.getInt("donation_id"));
                    donation.setHospitalName(rs.getString("hospital_name"));
                    donation.setUnits(rs.getInt("units"));
                    donation.setAppointmentDate(rs.getDate("appointment_date"));
                    donation.setStatus(rs.getString("status"));
                    history.add(donation);
                }
            }
        }
        return history;
    }

    private static Donation getDonationById(int donationId, Connection con) throws SQLException {
        String sql = "SELECT user_id, hospital_id, blood_group, units, appointment_date FROM donations WHERE donation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Donation donation = new Donation();
                    donation.setUserId(rs.getInt("user_id"));
                    donation.setHospitalId(rs.getInt("hospital_id"));
                    donation.setBloodGroup(rs.getString("blood_group"));
                    donation.setUnits(rs.getInt("units"));
                    donation.setAppointmentDate(rs.getDate("appointment_date"));
                    return donation;
                }
            }
        }
        return null;
    }

    public static Donation getDonationById(int donationId) throws Exception {
        String sql = "SELECT * FROM donations WHERE donation_id = ?";
        Donation donation = null;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    donation = new Donation();
                    donation.setDonationId(rs.getInt("donation_id"));
                    donation.setUserId(rs.getInt("user_id"));
                    donation.setHospitalId(rs.getInt("hospital_id"));
                    // ... set other properties if needed
                }
            }
        }
        return donation;
    }

    public static int getDonationCountForUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM donations WHERE user_id = ? AND status = 'COMPLETED'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public static List<String[]> expiringWithinDays(int days) throws Exception {
        List<String[]> expiringDonations = new ArrayList<>();
        String sql = "SELECT h.name, d.blood_group, d.units, d.expiry_date FROM donations d " +
                     "JOIN hospitals h ON d.hospital_id = h.hospital_id " +
                     "WHERE d.status = 'APPROVED' AND d.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                     "ORDER BY d.expiry_date ASC";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] donationInfo = new String[4];
                    donationInfo[0] = rs.getString("name");
                    donationInfo[1] = rs.getString("blood_group");
                    donationInfo[2] = String.valueOf(rs.getInt("units"));
                    donationInfo[3] = rs.getDate("expiry_date").toString();
                    expiringDonations.add(donationInfo);
                }
            }
        }
        return expiringDonations;
    }

    public static int getDonationCountInPastYear(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM donations WHERE user_id = ? " +
                     "AND status = 'COMPLETED' " +
                     "AND donation_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public static void approveAndProcessDonation(int donationId, Date donationDate) throws Exception {
        // This method is now redundant and approveDonationTransaction should be used instead.
        // Kept for historical purposes or if other parts of the code still use it.
        // It's recommended to refactor any remaining calls to use the transactional method.
        approveDonationTransaction(donationId, donationDate);
    }


    public static List<Donation> getActionableDonationsForHospital(int hospitalId) throws Exception {
        List<Donation> appointments = new ArrayList<>();
        String sql = "SELECT d.donation_id, u.name as donor_name, d.status " +
                     "FROM donations d JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.hospital_id = ? AND d.status IN ('PENDING', 'PRE-SCREEN_PASSED') " +
                     "ORDER BY d.appointment_date ASC, d.status DESC";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donation appt = new Donation();
                    appt.setDonationId(rs.getInt("donation_id"));
                    appt.setDonorName(rs.getString("donor_name"));
                    appt.setStatus(rs.getString("status"));
                    appointments.add(appt);
                }
            }
        }
        return appointments;
    }

    public static int getDonationCountForUser(int userId, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM donations WHERE user_id = ? AND status = 'COMPLETED'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public static int getDonationCountInPastYear(int userId, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM donations WHERE user_id = ? " +
                     "AND status = 'COMPLETED' " +
                     "AND donation_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    
    /**
     * ✅ NEW & CRITICAL: Creates a donation record for an emergency fulfillment.
     * This is a transactional method that ensures a formal donation record is created,
     * the donor's eligibility dates are updated, their emergency status is expired,
     * and any relevant achievements are awarded.
     *
     * @param userId The ID of the emergency donor.
     * @param hospitalId The ID of the hospital fulfilling the request.
     * @param bloodGroup The blood group that was donated.
     * @throws Exception if the transaction fails.
     */
    public static void createEmergencyDonationTransaction(int userId, int hospitalId, String bloodGroup) throws Exception {
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false); // Start transaction

            java.sql.Date donationDate = new java.sql.Date(System.currentTimeMillis()); // Today's date

            // 1. Create a new record in the donations table
         // ✅ FIX: Record 0 units to flag this as a special (emergency) donation
            String insertSql = "INSERT INTO donations (user_id, hospital_id, units, blood_group, status, appointment_date, donation_date) VALUES (?, ?, 0, ?, 'COMPLETED', ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, hospitalId);
                ps.setString(3, bloodGroup);
                ps.setDate(4, donationDate);
                ps.setDate(5, donationDate);
                ps.executeUpdate();
            }

            // 2. Update the donor's main eligibility dates
            Date nextEligibleDate = Date.valueOf(donationDate.toLocalDate().plusDays(90));
            UserDAO.updateDonationDates(userId, donationDate, nextEligibleDate, con);

            // 3. Expire the donor's emergency volunteer status
            EmergencyDonorDAO.setDonorOnCooldown(userId);

            // 4. Check for and award achievements
            checkAndAwardAchievements(userId, con);

            con.commit(); // Commit the transaction

        } catch (Exception e) {
            if (con != null) {
                con.rollback(); // Roll back on any error
            }
            throw new SQLException("The emergency donation transaction failed. Reason: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }
}