package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing Users.
 * This class includes the critical isDonorEligible() method to enforce cooldown periods.
 */
public class UserDAO {

    /**
     * ✅ FINAL FIX: Checks if a donor is eligible to make a new donation.
     * A donor is eligible if their `next_eligible_date` is in the past or today.
     * @param userId The ID of the user to check.
     * @return true if the donor is eligible, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    public static boolean isDonorEligible(int userId) throws SQLException {
        // CURDATE() gets the current date from the database server.
        // A donor is eligible if their next eligible date is null (never donated) or on/before today.
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ? AND (next_eligible_date IS NULL OR next_eligible_date <= CURDATE())";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // If the count is 1, the user exists and is eligible.
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false; // Default to not eligible.
    }

    public static User findByEmailAndPassword(String email, String password) throws Exception {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setBloodGroup(rs.getString("blood_group"));
                    user.setLastDonationDate(rs.getDate("last_donation_date"));
                    user.setNextEligibleDate(rs.getDate("next_eligible_date"));
                    return user;
                }
            }
        }
        return null;
    }

    public static List<User> getAllDonors() throws Exception {
        List<User> donorList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'DONOR'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setBloodGroup(rs.getString("blood_group"));
                user.setRole(rs.getString("role"));
                donorList.add(user);
            }
        }
        return donorList;
    }

    public static List<User> getAllPatients() throws Exception {
        List<User> patientList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'PATIENT'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setBloodGroup(rs.getString("blood_group"));
                user.setRole(rs.getString("role"));
                patientList.add(user);
            }
        }
        return patientList;
    }

    public static User getUserById(int userId) throws Exception {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setBloodGroup(rs.getString("blood_group"));
                    user.setRole(rs.getString("role"));
                    user.setLastDonationDate(rs.getDate("last_donation_date"));
                    user.setNextEligibleDate(rs.getDate("next_eligible_date"));
                    return user;
                }
            }
        }
        return null;
    }

    public static void updateUser(int userId, String name, String email, String bloodGroup) throws Exception {
        String sql = "UPDATE users SET name = ?, email = ?, blood_group = ? WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, bloodGroup);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public static void deleteUser(int userId) throws Exception {
        String deleteInventorySQL = "DELETE FROM blood_inventory WHERE donation_id IN (SELECT donation_id FROM donations WHERE user_id = ?)";
        String deleteCommentsSQL = "DELETE FROM community_comments WHERE post_id IN (SELECT post_id FROM community_posts WHERE user_id = ?)";
        String deleteAchievementsSQL = "DELETE FROM achievements WHERE user_id = ?";
        String deleteRequestsSQL = "DELETE FROM requests WHERE patient_id = ?";
        String deletePostsSQL = "DELETE FROM community_posts WHERE user_id = ?";
        String deleteDonationsSQL = "DELETE FROM donations WHERE user_id = ?";
        String deleteUserSQL = "DELETE FROM users WHERE user_id = ?";
        
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false); // Start transaction
            
            try (PreparedStatement ps = con.prepareStatement(deleteInventorySQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteCommentsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteAchievementsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteRequestsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deletePostsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteDonationsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteUserSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            
            con.commit();
            
        } catch (Exception e) {
            if (con != null) con.rollback();
            throw new ServletException("Error deleting user", e);
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public static boolean isEmailExists(String email) throws Exception {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static void insert(String name, String email, String password, String role, String bloodGroup, String contactNumber) throws Exception {
        String sql = "INSERT INTO users (name, email, password, role, blood_group, contact_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            if (bloodGroup != null && !bloodGroup.isEmpty()) {
                ps.setString(5, bloodGroup);
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }
            ps.setString(6, contactNumber);
            ps.executeUpdate();
        }
    }

    public static Date getNextEligibleDate(int userId) throws Exception {
        String sql = "SELECT next_eligible_date FROM users WHERE user_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("next_eligible_date");
                }
            }
        }
        return null;
    }

    public static void updateDonationDates(int userId, Date lastDonation, Date nextEligible, Connection con) throws SQLException {
        String sql = "UPDATE users SET last_donation_date = ?, next_eligible_date = ? WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, lastDonation);
            ps.setDate(2, nextEligible);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }
    
    public static void updateDonationDates(int userId, Date lastDonation, Date nextEligible) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            updateDonationDates(userId, lastDonation, nextEligible, con);
        }
    }
}