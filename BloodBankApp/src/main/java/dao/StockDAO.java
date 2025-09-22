package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StockDAO {

    /**
     * ✅ MODIFIED (Phase 4 - Hybrid):
     * This method now gets stock from BOTH tables and adds them together.
     * 1. Gets real, tracked bags from 'blood_inventory'.
     * 2. Gets manual adjustments from 'blood_stock'.
     */
    public static Map<String, Integer> getStockByHospital(int hospitalId) throws Exception {
        Map<String, Integer> stockLevels = new HashMap<>();
        
        // 1. Get real bags from 'blood_inventory' (only 'CLEARED' ones)
        String sqlInventory = "SELECT blood_group, COUNT(*) as units FROM blood_inventory " +
                              "WHERE hospital_id = ? AND inventory_status = 'CLEARED' " +
                              "GROUP BY blood_group";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlInventory)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stockLevels.put(rs.getString("blood_group"), rs.getInt("units"));
                }
            }
        }
        
        // 2. Get manual adjustments from 'blood_stock' and ADD them
        String sqlStock = "SELECT blood_group, units FROM blood_stock WHERE hospital_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlStock)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String bloodGroup = rs.getString("blood_group");
                    int manualUnits = rs.getInt("units");
                    // Add the manual units to the inventory units
                    stockLevels.put(bloodGroup, stockLevels.getOrDefault(bloodGroup, 0) + manualUnits);
                }
            }
        }
        return stockLevels;
    }
    
    /**
     * ✅ MODIFIED (Phase 4 - Hybrid):
     * Does the same as getStockByHospital, but for all hospitals (for the public dashboard).
     */
    public static Map<String, Integer> getAggregateStock() throws SQLException {
        Map<String, Integer> aggregateStock = new HashMap<>();
        
        // 1. Get real bags from 'blood_inventory'
        String sqlInventory = "SELECT blood_group, COUNT(*) AS total_units FROM blood_inventory " +
                              "WHERE inventory_status = 'CLEARED' GROUP BY blood_group";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlInventory);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                aggregateStock.put(rs.getString("blood_group"), rs.getInt("total_units"));
            }
        }
        
        // 2. Get manual adjustments from 'blood_stock' and ADD them
        String sqlStock = "SELECT blood_group, SUM(units) AS total_manual_units FROM blood_stock GROUP BY blood_group";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlStock);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String bloodGroup = rs.getString("blood_group");
                int manualUnits = rs.getInt("total_manual_units");
                aggregateStock.put(bloodGroup, aggregateStock.getOrDefault(bloodGroup, 0) + manualUnits);
            }
        }
        return aggregateStock;
    }

    /**
     * ✅ ADDED BACK (Phase 4 - Hybrid):
     * This method now *only* affects the 'blood_stock' table for manual adjustments.
     * Uses "INSERT...ON DUPLICATE KEY UPDATE" to safely add or update.
     */
    public static void addUnits(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = units + ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.setInt(4, units); // For the UPDATE part
            ps.executeUpdate();
        }
    }

    /**
     * ✅ ADDED BACK (Phase 4 - Hybrid):
     * This method now *only* affects the 'blood_stock' table for manual adjustments.
     */
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
     * ✅ NEW (Phase 4 - Hybrid):
     * This is the *new* method for consuming *real* inventory.
     * This will be called by ApproveRequestServlet.
     */
    public static void useInventoryBags(int hospitalId, String bloodGroup, int units) throws SQLException {
        String sql = "UPDATE blood_inventory SET inventory_status = 'USED' " +
                     "WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED' " +
                     "ORDER BY expiry_date ASC LIMIT ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected < units) {
                // Not enough real bags. We must take the remainder from the manual 'blood_stock' table.
                int remainder = units - rowsAffected;
                takeUnits(hospitalId, bloodGroup, remainder); // Call the *other* takeUnits method
            }
        }
    }

    /**
     * ✅ MODIFIED (Phase 4 - Hybrid):
     * This method still works perfectly, as it calls our new
     * hybrid getStockByHospital() method. No changes needed.
     */
    public static boolean isStockAvailable(int hospitalId, String bloodGroup, int units) throws SQLException {
        try {
            Map<String, Integer> stock = getStockByHospital(hospitalId);
            return stock.getOrDefault(bloodGroup, 0) >= units;
        } catch (Exception e) {
            throw new SQLException("Error checking stock availability", e);
        }
    }
    
    /**
     * ✅ ADDED BACK (Phase 4 - Hybrid):
     * This method now *only* affects the 'blood_stock' table for manual adjustments.
     */
    public static void setStock(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.setInt(4, units); // For the UPDATE part
            ps.executeUpdate();
        }
    }
}