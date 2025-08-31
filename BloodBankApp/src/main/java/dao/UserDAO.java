package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;

public class UserDAO {

	// In your UserDAO.java file, replace the findByEmailAndPassword method.

	public static User findByEmailAndPassword(String email, String password) throws Exception {
	    // ✅ MODIFIED: Added last_donation_date and next_eligible_date to the query
	    String sql = "SELECT * FROM users WHERE email=? AND password=?";
	    try (java.sql.Connection con = DBUtil.getConnection();
	         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, email);
	        ps.setString(2, password);
	        try (java.sql.ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                User user = new User();
	                user.setId(rs.getInt("user_id"));
	                user.setName(rs.getString("name"));
	                user.setEmail(rs.getString("email"));
	                user.setRole(rs.getString("role"));
	                user.setBloodGroup(rs.getString("blood_group"));
	                // ✅ ADDED: Fetch and set the donation dates on the user object
	                user.setLastDonationDate(rs.getDate("last_donation_date"));
	                user.setNextEligibleDate(rs.getDate("next_eligible_date"));
	                return user;
	            }
	        }
	    }
	    return null;
	}

    public static List<User> getAllUsers() throws Exception {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role != 'ADMIN'"; 
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
                userList.add(user);
            }
        }
        return userList;
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
        String deleteRequestsSQL = "DELETE FROM requests WHERE patient_id = ?";
        String deleteDonationsSQL = "DELETE FROM donations WHERE user_id = ?";
        String deleteUserSQL = "DELETE FROM users WHERE user_id = ?";
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(deleteRequestsSQL)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(deleteDonationsSQL)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(deleteUserSQL)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
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
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        }
        return exists;
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

    public static void updateDonationDates(int userId, Date lastDonation, Date nextEligible) throws Exception {
        String sql = "UPDATE users SET last_donation_date=?, next_eligible_date=? WHERE user_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, lastDonation);
            ps.setDate(2, nextEligible);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }
}