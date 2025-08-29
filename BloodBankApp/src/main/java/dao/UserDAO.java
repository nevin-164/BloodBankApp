package dao;
import model.User;
import java.sql.*;

public class UserDAO {

    public static User findByEmailAndPassword(String email, String password) throws Exception {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                u.setBloodGroup(rs.getString("blood_group"));
                u.setLastDonationDate(rs.getDate("last_donation_date"));
                u.setNextEligibleDate(rs.getDate("next_eligible_date"));
                return u;
            }
        }
    }

    public static void insert(String name, String email, String password, String role, String bg) throws Exception {
        String sql = "INSERT INTO users(name,email,password,role,blood_group) VALUES(?,?,?,?,?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.setString(5, bg);
            ps.executeUpdate();
        }
    }

    public static Date getNextEligibleDate(int userId) throws Exception {
        String sql = "SELECT next_eligible_date FROM users WHERE user_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDate(1) : null;
            }
        }
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
