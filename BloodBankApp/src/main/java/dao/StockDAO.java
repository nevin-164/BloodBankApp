package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StockDAO {

    public static Map<String, Integer> getStockByHospital(int hospitalId) throws Exception {
        Map<String, Integer> stockLevels = new HashMap<>();
        String sql = "SELECT blood_group, units FROM blood_stock WHERE hospital_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stockLevels.put(rs.getString("blood_group"), rs.getInt("units"));
                }
            }
        }
        return stockLevels;
    }

    public static void addUnits(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "UPDATE blood_stock SET units = units + ? WHERE hospital_id = ? AND blood_group = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, units);
            ps.setInt(2, hospitalId);
            ps.setString(3, bloodGroup);
            ps.executeUpdate();
        }
    }

    public static void takeUnits(int hospitalId, String bloodGroup, int units) throws SQLException {
        String sql = "UPDATE blood_stock SET units = units - ? WHERE hospital_id = ? AND blood_group = ? AND units >= ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, units);
            ps.setInt(2, hospitalId);
            ps.setString(3, bloodGroup);
            ps.setInt(4, units);
            ps.executeUpdate();
        }
    }

    /**
     * ✅ FIXED: Rewritten to solve the "Unreachable code" compilation error.
     */
    public static boolean isStockAvailable(int hospitalId, String bloodGroup, int units) throws SQLException {
        boolean available = false; // 1. Start with a default value
        String sql = "SELECT units FROM blood_stock WHERE hospital_id = ? AND blood_group = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            
            try (ResultSet rs = ps.executeQuery()) {
                // 2. Update the value only if a record is found and units are sufficient
                if (rs.next()) {
                    available = rs.getInt("units") >= units;
                }
            }
        }
        
        // 3. Return the final value at the end of the method
        return available;
    }
    
    public static void setStock(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "UPDATE blood_stock SET units = ? WHERE hospital_id = ? AND blood_group = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, units);
            ps.setInt(2, hospitalId);
            ps.setString(3, bloodGroup);
            ps.executeUpdate();
        }
    }
}