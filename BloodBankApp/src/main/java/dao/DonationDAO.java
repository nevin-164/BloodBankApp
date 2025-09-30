package dao;

import model.Donation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing donations.
 * This version has been corrected to resolve all compilation errors.
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

    // --- Public Methods (Safe for Servlets to Call) ---

    public static void approveDonationTransaction(int donationId, Date dateDonated) throws SQLException {
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false);

            Donation donation = getDonationById(donationId, con);
            if (donation == null) {
                throw new SQLException("Donation with ID " + donationId + " not found.");
            }

            setDonationCompletionDate(donationId, dateDonated, con);
            BloodInventoryDAO.clearPendingBagsForDonation(donationId, con);
            updateDonationStatus(donationId, "APPROVED", con);
            Date nextEligibleDate = Date.valueOf(dateDonated.toLocalDate().plusDays(90));
            UserDAO.updateDonationDates(donation.getUserId(), dateDonated, nextEligibleDate, con);

            con.commit();

        } catch (SQLException e) {
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
            for (int i = 0; i < donation.getUnits(); i++) {
                BloodInventoryDAO.addBag(donationId, donation.getHospitalId(), donation.getBloodGroup(), donation.getAppointmentDate(), con);
            }

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
        try (Connection con = DBUtil.getConnection()) {
            return getDonationById(donationId, con);
        }
    }

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

    /**
     * ✅ FIXED: This method now correctly returns a List<String[]> as declared.
     */
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
 // In dao/DonationDAO.java

    /**
     * ✅ NEW METHOD: Fetches all actionable donation appointments for a hospital.
     * This includes donations that are 'PENDING' or 'PRE-SCREEN_PASSED', ensuring
     * that no donation is lost from the hospital's view during the workflow.
     *
     * @param hospitalId The ID of the hospital.
     * @return A list of Donation objects that require action.
     * @throws Exception if a database error occurs.
     */
    public static List<Donation> getActionableDonationsForHospital(int hospitalId) throws Exception {
        List<Donation> appointments = new ArrayList<>();
        String sql = "SELECT d.donation_id, u.name as donor_name, d.blood_group, d.units, d.appointment_date, d.status " +
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
}