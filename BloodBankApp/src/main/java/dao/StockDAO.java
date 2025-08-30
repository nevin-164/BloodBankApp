package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StockDAO {

    public static Map<String, Integer> getAllStock() throws Exception {
        Map<String, Integer> stockLevels = new HashMap<>();
        // ✅ FIXED: Changed table name from 'stock' to 'blood_stock'
        String sql = "SELECT blood_group, units FROM blood_stock";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stockLevels.put(rs.getString("blood_group"), rs.getInt("units"));
            }
        }
        return stockLevels;
    }

    public static void addUnits(String bloodGroup, int units) throws Exception {
        // ✅ FIXED: Changed table name from 'stock' to 'blood_stock'
        String sql = "UPDATE blood_stock SET units = units + ? WHERE blood_group = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, units);
            ps.setString(2, bloodGroup);
            ps.executeUpdate();
        }
    }

    public static void removeStock(String bloodGroup, int units) throws Exception {
        // ✅ FIXED: Changed table name from 'stock' to 'blood_stock'
        String sql = "UPDATE blood_stock SET units = units - ? WHERE blood_group = ? AND units >= ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, units);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.executeUpdate();
        }
    }
    
    public static void setStock(String bloodGroup, int units) throws Exception {
        // ✅ FIXED: Changed table name from 'stock' to 'blood_stock'
        String sql = "UPDATE blood_stock SET units = ? WHERE blood_group = ?";
         try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, units);
            ps.setString(2, bloodGroup);
            ps.executeUpdate();
        }
    }

    public static boolean takeUnits(String bloodGroup, int units) throws SQLException {
        if (!isStockAvailable(bloodGroup, units)) {
            return false;
        }
        // ✅ FIXED: Changed table name from 'stock' to 'blood_stock'
        String sql = "UPDATE blood_stock SET units = units - ? WHERE blood_group = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, units);
            ps.setString(2, bloodGroup);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public static boolean isStockAvailable(String bloodGroup, int units) throws SQLException {
        // ✅ FIXED: Changed table name from 'stock' to 'blood_stock'
        String sql = "SELECT units FROM blood_stock WHERE blood_group = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bloodGroup);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("units") >= units;
            }
        }
    }
}