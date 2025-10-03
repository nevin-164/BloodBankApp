package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing emergency donors.
 * This version corrects the SQL query to use the correct 'start_date' column name,
 * resolving the "Unknown column" database error.
 */
public class EmergencyDonorDAO {

    /**
     * ✅ FIXED: Signs a donor up for a 7-day emergency period using the correct 'start_date' column.
     * Uses ON DUPLICATE KEY UPDATE to cleanly handle cases where a donor re-signs up.
     */
    public static void signUp(int userId, String bloodGroup) throws SQLException {
        String sql = "INSERT INTO emergency_donors (user_id, blood_group, start_date, expiry_date) " +
                     "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY)) " +
                     "ON DUPLICATE KEY UPDATE start_date = CURDATE(), expiry_date = DATE_ADD(CURDATE(), INTERVAL 7 DAY)";
        try (Connection con = DBUtil.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, bloodGroup);
            ps.executeUpdate();
        }
    }

    /**
     * Checks for and returns the expiry date if a donor is currently an active emergency donor.
     * Returns null if the donor is not active.
     */
    public static Date getEmergencyStatusExpiry(int userId) throws SQLException {
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
     * Gets a list of all ACTIVE emergency donors.
     * The `WHERE ed.expiry_date >= CURDATE()` clause ensures expired donors are not shown.
     */
 // Inside EmergencyDonorDAO.java

    /**
     * ✅ FINAL & CRITICAL FIX: Gets a list of all active emergency donors.
     * This version uses the correct SQL JOIN to retrieve all necessary donor details,
     * including their name, which was the root cause of the display issue.
     *
     * @return A list of User objects for all active emergency donors.
     * @throws Exception if a database error occurs.
     */
    public static List<User> getActiveEmergencyDonors() throws Exception {
        List<User> emergencyDonors = new ArrayList<>();
        // ✅ CRITICAL FIX: The SQL query now correctly joins the 'users' table
        // to fetch the donor's name and other essential details.
        String sql = "SELECT u.user_id, u.name, u.email, u.contact_number, u.blood_group " +
                     "FROM users u " +
                     "JOIN emergency_donors ed ON u.user_id = ed.user_id " +
                     "WHERE ed.expiry_date >= CURDATE()";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                User donor = new User();
                donor.setId(rs.getInt("user_id"));
                donor.setName(rs.getString("name")); // This will now work correctly
                donor.setEmail(rs.getString("email"));
                donor.setContactNumber(rs.getString("contact_number"));
                donor.setBloodGroup(rs.getString("blood_group"));
                emergencyDonors.add(donor);
            }
        }
        return emergencyDonors;
    }
    
    /**
     * Puts a donor on cooldown by expiring their current emergency sign-up immediately.
     */
    public static void setDonorOnCooldown(int userId) throws SQLException {
        String sql = "UPDATE emergency_donors SET expiry_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY) WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}