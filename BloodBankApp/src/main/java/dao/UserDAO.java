package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<User> getAllUsers() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, name, email, blood_group, role FROM users";  
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setBloodGroup(rs.getString("blood_group"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        }
        return list;
    }

    public static void updateUser(int userId, String name, String email, String bloodGroup) throws Exception {
        String sql = "UPDATE users SET name=?, email=?, blood_group=? WHERE user_id=?";
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
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

//Get next eligible donation date of a user
public static java.sql.Date getNextEligibleDate(int userId) throws Exception {
 String sql = "SELECT next_eligible_date FROM users WHERE user_id=?";
 try (Connection con = DBUtil.getConnection();
      PreparedStatement ps = con.prepareStatement(sql)) {
     ps.setInt(1, userId);
     try (ResultSet rs = ps.executeQuery()) {
         return rs.next() ? rs.getDate(1) : null;
     }
 }
}

//Update last donation date and next eligible date for a user
public static void updateDonationDates(int userId, java.sql.Date lastDonation, java.sql.Date nextEligible) throws Exception {
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