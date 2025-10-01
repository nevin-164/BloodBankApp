package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * ✅ FINAL VERSION: Implements a "Hybrid Model" for stock management.
 * This DAO correctly calculates total stock by combining data from two tables:
 * 1. `blood_inventory`: Tracks real, individual blood bags.
 * 2. `blood_stock`: Acts as a ledger for manual adjustments.
 */
public class StockDAO {

    /**
     * ✅ FIXED: Calculates the total usable stock by combining data from both inventory systems.
     * This method now initializes a complete map of all blood types to 0, ensuring that the
     * dashboard display is always consistent and reflects the true total sum from both the
     * physical inventory and the manual stock ledger. This resolves the issue where the
     * dashboard would not update after a manual stock change.
     */
    public static Map<String, Integer> getStockByHospital(int hospitalId) throws Exception {
        Map<String, Integer> stockLevels = new HashMap<>();
        String[] allBloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        
        // Step 1: Initialize the map with all blood groups to guarantee a complete view.
        for (String bg : allBloodGroups) {
            stockLevels.put(bg, 0);
        }

        // Use a single database connection for both queries for efficiency.
        try (Connection con = DBUtil.getConnection()) {
            
            // Step 2: Get the count of real, traceable bags from the primary inventory system.
            String sqlInventory = "SELECT blood_group, COUNT(*) as units FROM blood_inventory " +
                                  "WHERE hospital_id = ? AND inventory_status = 'CLEARED' " +
                                  "GROUP BY blood_group";
            
            try (PreparedStatement ps = con.prepareStatement(sqlInventory)) {
                ps.setInt(1, hospitalId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // Update the map with the count of physical bags.
                        stockLevels.put(rs.getString("blood_group"), rs.getInt("units"));
                    }
                }
            }
            
            // Step 3: Get the manually adjusted numbers from the ledger and add them to the total.
            String sqlStock = "SELECT blood_group, units FROM blood_stock WHERE hospital_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlStock)) {
                ps.setInt(1, hospitalId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String bloodGroup = rs.getString("blood_group");
                        int manualUnits = rs.getInt("units");
                        // Add the manual units to the existing count for that blood group.
                        stockLevels.put(bloodGroup, stockLevels.get(bloodGroup) + manualUnits);
                    }
                }
            }
        }
        return stockLevels;
    }
    
    /**
     * Calculates the total aggregate stock across all hospitals for the public dashboard.
     */
    public static Map<String, Integer> getAggregateStock() throws SQLException {
        Map<String, Integer> aggregateStock = new HashMap<>();
        
        try (Connection con = DBUtil.getConnection()) {
            // Step 1: Get total real bags from the primary inventory.
            String sqlInventory = "SELECT blood_group, COUNT(*) AS total_units FROM blood_inventory " +
                                  "WHERE inventory_status = 'CLEARED' GROUP BY blood_group";
            try (PreparedStatement ps = con.prepareStatement(sqlInventory);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aggregateStock.put(rs.getString("blood_group"), rs.getInt("total_units"));
                }
            }
            
            // Step 2: Get total manual adjustments from the ledger and add them.
            String sqlStock = "SELECT blood_group, SUM(units) AS total_manual_units FROM blood_stock GROUP BY blood_group";
            try (PreparedStatement ps = con.prepareStatement(sqlStock);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String bloodGroup = rs.getString("blood_group");
                    int manualUnits = rs.getInt("total_manual_units");
                    aggregateStock.put(bloodGroup, aggregateStock.getOrDefault(bloodGroup, 0) + manualUnits);
                }
            }
        }
        return aggregateStock;
    }

    /**
     * Adds units to the manual adjustment ledger (`blood_stock` table).
     */
    public static void addUnits(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = units + ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.setInt(4, units); // Value for the "UPDATE" part.
            ps.executeUpdate();
        }
    }

    /**
     * Subtracts units from the manual adjustment ledger (`blood_stock` table).
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
     * Consumes blood, prioritizing real inventory first, then the manual ledger.
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
            
            // If not enough real bags were used, take the rest from the manual ledger.
            if (rowsAffected < units) {
                int remainder = units - rowsAffected;
                takeUnits(hospitalId, bloodGroup, remainder);
            }
        }
    }

    /**
     * Checks if the total combined stock is sufficient for a request.
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
     * Sets the value in the manual adjustment ledger (`blood_stock`) to a specific number.
     */
    public static void setStock(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.setInt(4, units); // Value for the "UPDATE" part.
            ps.executeUpdate();
        }
    }
}