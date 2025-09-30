package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing emergency donors.
 * This version corrects the logic to store the donor's blood group upon sign-up.
 */
public class EmergencyDonorDAO {

    /**
     * ✅ FIXED: Signs a donor up for a 7-day period for a specific blood group.
     * This method now accepts and stores the blood group, which is the critical fix.
     */
    public static void signUp(int userId, String bloodGroup) throws Exception {
        // NOTE: This assumes your `emergency_donors` table has a `blood_group` column.
        String sql = "INSERT INTO emergency_donors (user_id, blood_group, start_date, expiry_date) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY)) " +
                     "ON DUPLICATE KEY UPDATE blood_group = ?, start_date = CURDATE(), expiry_date = DATE_ADD(CURDATE(), INTERVAL 7 DAY)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, bloodGroup);
            ps.setString(3, bloodGroup); // For the ON DUPLICATE KEY UPDATE part
            ps.executeUpdate();
        }
    }

    /**
     * Checks if a donor is currently an active emergency donor.
     */
    public static Date getEmergencyStatusExpiry(int userId) throws Exception {
        String sql = "SELECT expiry_date FROM emergency_donors WHERE user_id = ? AND expiry_date >= CURDATE()";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("expiry_date");
                }
            }
        }
        return null;
    }

    /**
     * ✅ FIXED: Gets a list of all active emergency donors for a specific blood group.
     */
    public static List<User> getAvailableEmergencyDonors(String bloodGroup) throws Exception {
        List<User> emergencyDonors = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.email, u.contact_number FROM users u " +
                     "JOIN emergency_donors ed ON u.user_id = ed.user_id " +
                     "WHERE ed.blood_group = ? AND ed.expiry_date >= CURDATE()";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bloodGroup);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User donor = new User();
                    donor.setId(rs.getInt("user_id"));
                    donor.setName(rs.getString("name"));
                    donor.setEmail(rs.getString("email"));
                    donor.setContactNumber(rs.getString("contact_number"));
                    emergencyDonors.add(donor);
                }
            }
        }
        return emergencyDonors;
    }
}