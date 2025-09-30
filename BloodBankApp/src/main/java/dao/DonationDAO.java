package dao;

import model.Donation;
import model.BloodInventory;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing donations.
 * This class handles all database interactions for the 'donations' table.
 * It is designed to be safe and robust, using transactions for complex operations
 * to prevent data inconsistency.
 */
public class DonationDAO {

    // --- Private Helper Methods (For Internal Use Only) ---

    /**
     * INTERNAL METHOD: Updates the status of a donation within a transaction.
     * This is private to ensure it is only ever called safely from the approveDonationTransaction method.
     */
    private static void updateDonationStatus(int donationId, String status, Connection con) throws SQLException {
        String sql = "UPDATE donations SET status = ? WHERE donation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, donationId);
            ps.executeUpdate();
        }
    }

    /**
     * INTERNAL METHOD: Sets the actual donation date within a transaction.
     * This is private to ensure it is only ever called safely from the approveDonationTransaction method.
     */
    private static void setDonationCompletionDate(int donationId, java.sql.Date actualDonationDate, Connection con) throws SQLException {
        String sql = "UPDATE donations SET donation_date = ?, expiry_date = DATE_ADD(?, INTERVAL 42 DAY) WHERE donation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, actualDonationDate);
            ps.setDate(2, actualDonationDate);
            ps.setInt(3, donationId);
            ps.executeUpdate();
        }
    }

    // --- Public Methods (Safe for Servlets to Call) ---

    /**
     * Performs the entire donation approval process within a secure "all-or-nothing" transaction.
     * This is the ONLY method that should be called to approve a donation. If any step fails,
     * all previous steps in the process are automatically undone.
     *
     * @param donationId The ID of the donation to approve.
     * @param dateDonated The actual date the donation occurred.
     * @throws SQLException if any database operation fails, triggering a rollback.
     */
    public static void approveDonationTransaction(int donationId, Date dateDonated) throws SQLException {
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false); // Start the transaction

            Donation donation = getDonationById(donationId, con);
            if (donation == null) throw new SQLException("Donation with ID " + donationId + " not found.");

            // Step 1: Set the completion date.
            setDonationCompletionDate(donationId, dateDonated, con);

            // Step 2: Add individual bags to the detailed inventory.
            for (int i = 0; i < donation.getUnits(); i++) {
                BloodInventoryDAO.addBag(donationId, donation.getHospitalId(), donation.getBloodGroup(), dateDonated, con);
            }

            // Step 3: Update the main donation status.
            updateDonationStatus(donationId, "APPROVED", con);

            // Step 4: Update the donor's eligibility cooldown period.
            Date nextEligibleDate = Date.valueOf(dateDonated.toLocalDate().plusDays(90));
            UserDAO.updateDonationDates(donation.getUserId(), dateDonated, nextEligibleDate, con);

            con.commit(); // If all steps succeeded, permanently save all changes.

        } catch (SQLException e) {
            if (con != null) con.rollback(); // If any step failed, undo everything.
            throw e; // Pass the error up to the servlet.
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }
    
    /**
     * A dedicated public method for declining a donation appointment.
     * This is a simple, single operation and does not require a complex transaction.
     *
     * @param donationId The ID of the donation to be declined.
     * @throws SQLException if a database access error occurs.
     */
    public static void declineDonation(int donationId) throws SQLException {
        String sql = "UPDATE donations SET status = 'DECLINED' WHERE donation_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            ps.executeUpdate();
        }
    }

    /**
     * A dedicated public method for clearing a user's notification.
     * It changes the status from APPROVED/DECLINED to COMPLETED/CLOSED.
     *
     * @param donationId The ID of the donation whose notification is to be cleared.
     * @throws SQLException if a database access error occurs.
     */
    public static void clearDonationNotification(int donationId) throws SQLException {
        String sql = "UPDATE donations SET status = CASE " +
                     "WHEN status = 'APPROVED' THEN 'COMPLETED' " +
                     "WHEN status = 'DECLINED' THEN 'CLOSED' " +
                     "END " +
                     "WHERE donation_id = ? AND (status = 'APPROVED' OR status = 'DECLINED')";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            ps.executeUpdate();
        }
    }
    
    /**
     * ✅ NEW PUBLIC METHOD: For a hospital to update a donation status.
     * This fixes the compile error in UpdateDonationStatusServlet.
     */
    public static void updateDonationStatusForHospital(int donationId, String newStatus) throws SQLException {
        String sql = "UPDATE donations SET status = ? WHERE donation_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, donationId);
            ps.executeUpdate();
        }
    }

    /**
     * Creates a new pending donation appointment.
     * This version includes the workaround for databases where 'donation_date' cannot be null.
     *
     * @param userId The ID of the donor.
     * @param hospitalId The ID of the target hospital.
     * @param units The number of units offered.
     * @param appointmentDate The requested date for the appointment.
     * @throws Exception if a database error occurs.
     */
    public static void createDonationAppointment(int userId, int hospitalId, int units, java.sql.Date appointmentDate) throws Exception {
        String insertSql = "INSERT INTO donations (user_id, hospital_id, units, blood_group, status, appointment_date, donation_date) " +
                           "VALUES (?, ?, ?, (SELECT blood_group FROM users WHERE user_id = ?), 'PENDING', ?, CURDATE())";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement psInsert = con.prepareStatement(insertSql)) {
            psInsert.setInt(1, userId);
            psInsert.setInt(2, hospitalId);
            psInsert.setInt(3, units);
            psInsert.setInt(4, userId);
            psInsert.setDate(5, appointmentDate);
            psInsert.executeUpdate();
        }
    }

    
    // --- Data Retrieval Methods ---

    /**
     * Fetches the donor's currently active pending appointment.
     *
     * @param userId The ID of the donor.
     * @return A Donation object if a pending appointment exists, otherwise null.
     * @throws Exception If a database error occurs.
     */
    public static Donation getPendingAppointmentForDonor(int userId) throws Exception {
        String sql = "SELECT d.appointment_date, h.name as hospital_name, d.status FROM donations d " +
                     "JOIN hospitals h ON d.hospital_id = h.hospital_id " +
                     "WHERE d.user_id = ? AND d.status = 'PENDING'";
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

    /**
     * Fetches all pending donation appointments for a specific hospital.
     *
     * @param hospitalId The ID of the hospital.
     * @return A list of pending Donation objects.
     * @throws Exception If a database error occurs.
     */
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

    /**
     * Fetches the complete donation history for a single user. Used for the donor dashboard.
     *
     * @param userId The ID of the user whose history is being fetched.
     * @return A list of the user's past and present donations.
     * @throws SQLException if a database error occurs.
     */
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

    // Internal getDonationById that uses an existing connection (needed for transactions)
    private static Donation getDonationById(int donationId, Connection con) throws SQLException {
        String sql = "SELECT user_id, hospital_id, blood_group, units FROM donations WHERE donation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Donation donation = new Donation();
                    donation.setUserId(rs.getInt("user_id"));
                    donation.setHospitalId(rs.getInt("hospital_id"));
                    donation.setBloodGroup(rs.getString("blood_group"));
                    donation.setUnits(rs.getInt("units"));
                    return donation;
                }
            }
        }
        return null;
    }

    // Public getDonationById that creates its own connection
    public static Donation getDonationById(int donationId) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            return getDonationById(donationId, con);
        }
    }
    
    /**
     * Gets the total number of completed donations for a specific user.
     *
     * @param userId The ID of the user.
     * @return The total count of approved/fulfilled donations.
     * @throws SQLException if a database error occurs.
     */
    public static int getDonationCountForUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM donations WHERE user_id = ? AND status = 'APPROVED'";
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
        // Joins with hospitals to also get the hospital name for the alert.
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
                    donationInfo[0] = rs.getString("name"); // Hospital Name
                    donationInfo[1] = rs.getString("blood_group");
                    donationInfo[2] = String.valueOf(rs.getInt("units"));
                    donationInfo[3] = rs.getDate("expiry_date").toString();
                    expiringDonations.add(donationInfo);
                }
            }
        }
        return expiringDonations;
    }

    /**
     * Gets the number of completed donations for a user within the past year.
     *
     * @param userId The ID of the user.
     * @return The count of donations in the last 365 days.
     * @throws SQLException if a database error occurs.
     */
    public static int getDonationCountInPastYear(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM donations WHERE user_id = ? " +
                     "AND status = 'APPROVED' " +
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
}