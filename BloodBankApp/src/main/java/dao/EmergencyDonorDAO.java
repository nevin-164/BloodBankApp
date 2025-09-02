package dao;

import model.User;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmergencyDonorDAO {

    // Signs a donor up for a 7-day period
    public static void signUp(int userId) throws Exception {
        String sql = "INSERT INTO emergency_donors (user_id, start_date, end_date) VALUES (?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY))";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // Checks if a donor is currently an active emergency donor
    public static Date getEmergencyStatusExpiry(int userId) throws Exception {
        String sql = "SELECT end_date FROM emergency_donors WHERE user_id = ? AND end_date >= CURDATE()";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("end_date");
                }
            }
        }
        return null;
    }

    // Gets a list of all active emergency donors for a specific blood group
    public static List<User> getAvailableEmergencyDonors(String bloodGroup) throws Exception {
        List<User> emergencyDonors = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.contact_number, u.blood_group " +
                     "FROM users u JOIN emergency_donors ed ON u.user_id = ed.user_id " +
                     "WHERE u.blood_group = ? AND ed.end_date >= CURDATE()";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bloodGroup);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User donor = new User();
                    donor.setId(rs.getInt("user_id"));
                    donor.setName(rs.getString("name"));
                    donor.setContactNumber(rs.getString("contact_number"));
                    donor.setBloodGroup(rs.getString("blood_group"));
                    emergencyDonors.add(donor);
                }
            }
        }
        return emergencyDonors;
    }
}