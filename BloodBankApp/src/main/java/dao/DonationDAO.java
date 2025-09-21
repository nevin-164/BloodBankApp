package dao;

import model.Donation;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List;
import java.util.Map;   

public class DonationDAO {

	public static void createDonationRequest(int userId, int hospitalId, int units, java.sql.Date appointmentDate) throws Exception {
	    
	    String cleanupSql = "UPDATE donations SET status = CASE " +
	                        "WHEN status = 'APPROVED' THEN 'COMPLETED' " +
	                        "WHEN status = 'DECLINED' THEN 'CLOSED' " +
	                        "END " +
	                        "WHERE user_id = ? AND (status = 'APPROVED' OR status = 'DECLINED')";

	    String insertSql = "INSERT INTO donations (user_id, hospital_id, units, blood_group, status, appointment_date, donation_date) " +
	                       "VALUES (?, ?, ?, (SELECT blood_group FROM users WHERE user_id = ?), 'PENDING', ?, ?)";
	    
	    java.sql.Date requestDate = java.sql.Date.valueOf(java.time.LocalDate.now());

	    try (java.sql.Connection con = DBUtil.getConnection()) {
	        try (java.sql.PreparedStatement psCleanup = con.prepareStatement(cleanupSql)) {
	            psCleanup.setInt(1, userId);
	            psCleanup.executeUpdate();
	        }

	        try (java.sql.PreparedStatement psInsert = con.prepareStatement(insertSql)) {
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

    public static model.Donation getDonationById(int donationId) throws Exception {
         String sql = "SELECT user_id, hospital_id, blood_group, units FROM donations WHERE donation_id = ?";
         try (java.sql.Connection con = DBUtil.getConnection();
              java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.Donation donation = new model.Donation();
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

    public static Map<String, Double> getAverageDailyDonations(int hospitalId) throws SQLException {
        Map<String, Double> avgDonations = new HashMap<>();
        String sql = "SELECT blood_group, AVG(units) AS avg_units FROM donations " +
                     "WHERE hospital_id = ? AND status = 'APPROVED' GROUP BY blood_group";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, hospitalId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    avgDonations.put(rs.getString("blood_group"), rs.getDouble("avg_units"));
                }
            }
        }
        return avgDonations;
    }
    
    public static int getDonationCountForUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM donations WHERE user_id = ? AND (status = 'APPROVED' OR status = 'FULFILLED')";
        int count = 0;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }

    /**
     * ✅ NEW: Added for 'Annual Donor' Badge.
     * Counts approved donations for a user within the past 365 days.
     */
    public static int getDonationCountInPastYear(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM donations WHERE user_id = ? " +
                     "AND (status = 'APPROVED' OR status = 'FULFILLED') " +
                     "AND donation_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";
        int count = 0;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }
}