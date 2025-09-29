package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This DAO now implements a "Hybrid Model" for stock management, using two tables.
 * 1. `blood_inventory`: Tracks real, individual blood bags with statuses. This is the primary, traceable stock.
 * 2. `blood_stock`: Acts as a simple ledger for manual adjustments and untracked bulk stock.
 */
public class StockDAO {

    /**
     * Calculates the total usable stock by combining data from both inventory systems.
     * It first counts CLEARED bags from the primary inventory, then adds any manual adjustments from the stock ledger.
     * @param hospitalId The ID of the hospital.
     * @return A map representing the total combined stock for each blood group.
     */
    public static Map<String, Integer> getStockByHospital(int hospitalId) throws Exception {
        Map<String, Integer> stockLevels = new HashMap<>();
        
        // Step 1: Get the count of real, traceable bags from the primary inventory system.
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
        
        // Step 2: Get the manually adjusted numbers from the ledger and add them to the total.
        String sqlStock = "SELECT blood_group, units FROM blood_stock WHERE hospital_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlStock)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String bloodGroup = rs.getString("blood_group");
                    int manualUnits = rs.getInt("units");
                    // Add the manual units to any existing tracked units for the same blood group.
                    stockLevels.put(bloodGroup, stockLevels.getOrDefault(bloodGroup, 0) + manualUnits);
                }
            }
        }
        return stockLevels;
    }
    
    /**
     * Calculates the total aggregate stock across all hospitals for the public dashboard.
     * This also uses the hybrid model, combining both inventory systems.
     * @return A map of the total stock for each blood group across the entire network.
     */
    public static Map<String, Integer> getAggregateStock() throws SQLException {
        Map<String, Integer> aggregateStock = new HashMap<>();
        
        // Step 1: Get total real bags from the primary inventory.
        String sqlInventory = "SELECT blood_group, COUNT(*) AS total_units FROM blood_inventory " +
                              "WHERE inventory_status = 'CLEARED' GROUP BY blood_group";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlInventory);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                aggregateStock.put(rs.getString("blood_group"), rs.getInt("total_units"));
            }
        }
        
        // Step 2: Get total manual adjustments from the ledger and add them.
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
     * Adds units to the manual adjustment ledger (`blood_stock` table).
     * This uses an "upsert" command to safely create a new record or add to an existing one.
     */
    public static void addUnits(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = units + ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.setInt(4, units); // This value is for the "UPDATE" part of the command.
            ps.executeUpdate();
        }
    }

    /**
     * Subtracts units from the manual adjustment ledger (`blood_stock` table).
     */
    public static void takeUnits(int hospitalId, String bloodGroup, int units) throws SQLException {
        // Includes a check to prevent stock from going below zero.
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
     * Consumes blood for a patient request, prioritizing real, tracked inventory first.
     * It attempts to mark real bags as 'USED'. If not enough are available, it subtracts the remainder
     * from the manual adjustment ledger.
     */
    public static void useInventoryBags(int hospitalId, String bloodGroup, int units) throws SQLException {
        // This query attempts to use up the real, tracked bags first.
        String sql = "UPDATE blood_inventory SET inventory_status = 'USED' " +
                     "WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED' " +
                     "ORDER BY expiry_date ASC LIMIT ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            
            int rowsAffected = ps.executeUpdate();
            
            // If the number of real bags used is less than what was needed,
            // we must take the rest from the manual ledger.
            if (rowsAffected < units) {
                int remainder = units - rowsAffected;
                takeUnits(hospitalId, bloodGroup, remainder); // This calls the ledger-specific method.
            }
        }
    }

    /**
     * Checks if the total combined stock (from both systems) is sufficient for a request.
     * This method works correctly with the hybrid model because it calls our updated getStockByHospital().
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
     * This is useful for admin corrections.
     */
    public static void setStock(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.setInt(4, units); // This value is for the "UPDATE" part.
            ps.executeUpdate();
        }
    }
}

