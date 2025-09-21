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
    
    /**
     * ✅ MODIFIED: This method now replaces getAllStock().
     * It returns the TOTAL aggregate stock for each blood type, system-wide.
     * This respects hospital privacy and is used for the bar graph.
     */
    public static Map<String, Integer> getAggregateStock() throws SQLException {
        Map<String, Integer> aggregateStock = new HashMap<>();
        // This SQL query now SUMS all units and groups them by blood type.
        String sql = "SELECT blood_group, SUM(units) AS total_units FROM blood_stock GROUP BY blood_group";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                aggregateStock.put(rs.getString("blood_group"), rs.getInt("total_units"));
            }
        }
        return aggregateStock;
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

    public static boolean isStockAvailable(int hospitalId, String bloodGroup, int units) throws SQLException {
        boolean available = false; 
        String sql = "SELECT units FROM blood_stock WHERE hospital_id = ? AND blood_group = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    available = rs.getInt("units") >= units;
                }
            }
        }
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