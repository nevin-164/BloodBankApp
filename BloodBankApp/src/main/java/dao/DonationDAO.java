package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {

    public static void insert(int userId, String bg, Date donationDate, Date expiryDate, int units, int hospitalId) 
            throws Exception {
        String sql = "INSERT INTO donations(user_id, blood_group, donation_date, expiry_date, units, tested_safe, hospital_id) " +
                     "VALUES (?, ?, ?, ?, ?, 1, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, bg);
            ps.setDate(3, donationDate);
            ps.setDate(4, expiryDate);
            ps.setInt(5, units);
            ps.setInt(6, hospitalId);
            ps.executeUpdate();
        }
    }

    public static List<String[]> expiringWithinDays(int days) throws Exception {
        String sql = "SELECT blood_group, donation_date, expiry_date, units FROM donations " +
                     "WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY) AND expiry_date >= CURDATE()";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                List<String[]> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new String[]{
                        rs.getString("blood_group"),
                        rs.getDate("donation_date").toString(),
                        rs.getDate("expiry_date").toString(),
                        String.valueOf(rs.getInt("units"))
                    });
                }
                return out;
            }
        }
    }
}
