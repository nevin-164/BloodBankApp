package dao;

import model.Donation;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {

    public static void createDonationRequest(int userId, int hospitalId, int units) throws Exception {
        String cleanupSql = "UPDATE donations SET status = CASE " +
                            "WHEN status = 'APPROVED' THEN 'COMPLETED' " +
                            "WHEN status = 'DECLINED' THEN 'CLOSED' " +
                            "END " +
                            "WHERE user_id = ? AND (status = 'APPROVED' OR status = 'DECLINED')";
        String insertSql = "INSERT INTO donations (user_id, hospital_id, units, blood_group, status, appointment_date, donation_date, expiry_date) " +
                           "VALUES (?, ?, ?, (SELECT blood_group FROM users WHERE user_id = ?), 'PENDING', ?, ?, NULL)";
        
        LocalDate today = LocalDate.now();
        Date appointmentDate = Date.valueOf(today.plusDays(2));
        Date requestDate = Date.valueOf(today);

        try (Connection con = DBUtil.getConnection()) {
            try (PreparedStatement psCleanup = con.prepareStatement(cleanupSql)) {
                psCleanup.setInt(1, userId);
                psCleanup.executeUpdate();
            }
            try (PreparedStatement psInsert = con.prepareStatement(insertSql)) {
                psInsert.setInt(1, userId);
                psInsert.setInt(2, hospitalId);
                psInsert.setInt(3, units);
                psInsert.setInt(4, userId);
                psInsert.setDate(5, appointmentDate);
                psInsert.setDate(6, requestDate);
                psInsert.executeUpdate();
            }
        }
    }

    public static Donation getPendingAppointmentForDonor(int userId) throws Exception {
        String sql = "SELECT d.appointment_date, h.name as hospital_name FROM donations d " +
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
                    return appointment;
                }
            }
        }
        return null;
    }

    public static List<Donation> getPendingDonations(int hospitalId) throws Exception {
        List<Donation> appointments = new ArrayList<>();
        String sql = "SELECT d.donation_id, u.name as donor_name, d.blood_group, d.units, d.appointment_date " +
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
                    appointments.add(appt);
                }
            }
        }
        return appointments;
    }

 // In your DonationDAO.java file, replace the getDonationById method.

    public static model.Donation getDonationById(int donationId) throws Exception {
         String sql = "SELECT user_id, hospital_id, blood_group, units FROM donations WHERE donation_id = ?";
         try (java.sql.Connection con = DBUtil.getConnection();
              java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.Donation donation = new model.Donation();
                    donation.setUserId(rs.getInt("user_id"));
                    // ✅ FIXED: Fetch and set the hospital_id
                    donation.setHospitalId(rs.getInt("hospital_id"));
                    donation.setBloodGroup(rs.getString("blood_group"));
                    donation.setUnits(rs.getInt("units"));
                    return donation;
                }
            }
        }
        return null;
    }

    public static List<String[]> expiringWithinDays(int days) throws Exception {
        List<String[]> expiringDonations = new ArrayList<>();
        String sql = "SELECT blood_group, units, expiry_date FROM donations " +
                     "WHERE status = 'APPROVED' AND expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                     "ORDER BY expiry_date ASC";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] donationInfo = new String[3];
                    donationInfo[0] = rs.getString("blood_group");
                    donationInfo[1] = String.valueOf(rs.getInt("units"));
                    donationInfo[2] = rs.getDate("expiry_date").toString();
                    expiringDonations.add(donationInfo);
                }
            }
        }
        return expiringDonations;
    }

    public static void updateDonationStatus(int donationId, String status) throws Exception {
        String sql = "UPDATE donations SET status = ? WHERE donation_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, donationId);
            ps.executeUpdate();
        }
    }

    public static Donation getLatestDonationUpdateForDonor(int userId) throws Exception {
        String sql = "SELECT d.status, d.donation_id, h.name as hospital_name FROM donations d " +
                     "JOIN hospitals h ON d.hospital_id = h.hospital_id " +
                     "WHERE d.user_id = ? AND (d.status = 'APPROVED' OR d.status = 'DECLINED') " +
                     "ORDER BY d.donation_date DESC, d.appointment_date DESC LIMIT 1";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Donation notification = new Donation();
                    notification.setDonationId(rs.getInt("donation_id"));
                    notification.setStatus(rs.getString("status"));
                    notification.setHospitalName(rs.getString("hospital_name"));
                    return notification;
                }
            }
        }
        return null;
    }
}