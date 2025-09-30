package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing Users.
 * This class handles all database interactions for the 'users' table.
 * It is designed to be safe, using transactions for complex operations like deletion,
 * and includes the critical overloaded method to participate in transactions managed by other DAOs.
 */
public class UserDAO {

    /**
     * Finds a user by their email and password for login authentication.
     * @param email The user's email address.
     * @param password The user's plain-text password. NOTE: In a real-world application, this should be hashed.
     * @return A User object if credentials are valid, otherwise null.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * Retrieves a list of all users with the 'DONOR' role.
     * @return A List of User objects.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * Retrieves a list of all users with the 'PATIENT' role.
     * @return A List of User objects.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * Fetches a single user's details by their unique ID.
     * @param userId The ID of the user to find.
     * @return A User object if found, otherwise null.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * Updates a user's profile information.
     * @param userId The ID of the user to update.
     * @param name The new name for the user.
     * @param email The new email for the user.
     * @param bloodGroup The new blood group for the user.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * Deletes a user and all their associated data from the database within a transaction.
     * This method safely deletes related records in the correct order to avoid foreign key errors.
     * @param userId The ID of the user to delete.
     * @throws Exception if the deletion fails, triggering a rollback.
     */
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
            
            // Execute deletes in the correct order to respect foreign key constraints
            try (PreparedStatement ps = con.prepareStatement(deleteInventorySQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteCommentsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteAchievementsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteRequestsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deletePostsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteDonationsSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(deleteUserSQL)) { ps.setInt(1, userId); ps.executeUpdate(); }
            
            con.commit(); // Commit all changes if no errors occurred
            
        } catch (Exception e) {
            if (con != null) con.rollback(); // Rollback all changes if an error occurred
            throw new ServletException("Error deleting user", e);
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /**
     * Checks if an email address already exists in the database.
     * @param email The email to check.
     * @return true if the email exists, false otherwise.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * Inserts a new user into the database during registration.
     * @param name The user's full name.
     * @param email The user's email address.
     * @param password The user's plain-text password.
     * @param role The user's role ('DONOR' or 'PATIENT').
     * @param bloodGroup The user's blood group.
     * @param contactNumber The user's contact number.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * Retrieves the next date a donor is eligible to donate.
     * @param userId The ID of the donor.
     * @return A Date object representing the next eligible date, or null if not set.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * Updates a donor's last donation and next eligible dates. This version creates its own connection.
     * @param userId The ID of the donor to update.
     * @param lastDonation The date of the last successful donation.
     * @param nextEligible The new calculated date for next eligibility.
     * @throws Exception if a database error occurs.
     */
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

    /**
     * ✅ OVERLOADED VERSION FOR TRANSACTIONS: This is the critical fix.
     * This version of updateDonationDates accepts and uses an existing database connection,
     * allowing it to safely participate in the "all-or-nothing" transaction started in DonationDAO.
     *
     * @param userId The ID of the user to update.
     * @param lastDonation The date of the last donation.
     * @param nextEligible The calculated next eligible date for donation.
     * @param con The existing database connection to use for the transaction.
     * @throws SQLException if a database access error occurs.
     */
    public static void updateDonationDates(int userId, Date lastDonation, Date nextEligible, Connection con) throws SQLException {
        String sql = "UPDATE users SET last_donation_date=?, next_eligible_date=? WHERE user_id=?";
        // This 'try-with-resources' only manages the PreparedStatement; the Connection is managed by the calling method.
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, lastDonation);
            ps.setDate(2, nextEligible);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }
}

