package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * ✅ DEFINITIVE FINAL VERSION: Implements a "Hybrid Model" for stock management.
 * This DAO correctly calculates total stock by combining data from two tables:
 * 1. `blood_inventory`: Tracks real, individual blood bags from donations (Traceable).
 * 2. `blood_stock`: Acts as a ledger for untraceable or manual adjustments.
 */
public class StockDAO {

    /**
     * Calculates the total usable stock by combining data from both inventory systems.
     * This is what the dashboard displays.
     */
    public static Map<String, Integer> getStockByHospital(int hospitalId) throws Exception {
        Map<String, Integer> stockLevels = new HashMap<>();
        String[] allBloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        
        for (String bg : allBloodGroups) {
            stockLevels.put(bg, 0);
        }

        try (Connection con = DBUtil.getConnection()) {
            
            // Step 1: Get the count of real, traceable bags from the physical inventory.
            String sqlInventory = "SELECT blood_group, COUNT(*) as units FROM blood_inventory " +
                                  "WHERE hospital_id = ? AND inventory_status = 'CLEARED' " +
                                  "GROUP BY blood_group";
            
            try (PreparedStatement ps = con.prepareStatement(sqlInventory)) {
                ps.setInt(1, hospitalId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        stockLevels.put(rs.getString("blood_group"), rs.getInt("units"));
                    }
                }
            }
            
            // Step 2: Get the manually adjusted numbers and ADD them to the total.
            String sqlStock = "SELECT blood_group, units FROM blood_stock WHERE hospital_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlStock)) {
                ps.setInt(1, hospitalId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String bloodGroup = rs.getString("blood_group");
                        int manualUnits = rs.getInt("units");
                        stockLevels.put(bloodGroup, stockLevels.get(bloodGroup) + manualUnits);
                    }
                }
            }
        }
        return stockLevels;
    }
    
    // ... all other methods in StockDAO remain the same ...

    public static Map<String, Integer> getAggregateStock() throws SQLException {
        Map<String, Integer> aggregateStock = new HashMap<>();
        try (Connection con = DBUtil.getConnection()) {
            String sqlInventory = "SELECT blood_group, COUNT(*) AS total_units FROM blood_inventory " +
                                  "WHERE inventory_status = 'CLEARED' GROUP BY blood_group";
            try (PreparedStatement ps = con.prepareStatement(sqlInventory);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aggregateStock.put(rs.getString("blood_group"), rs.getInt("total_units"));
                }
            }
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

    public static void addUnits(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = units + ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.setInt(4, units);
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
                int remainder = units - rowsAffected;
                takeUnits(hospitalId, bloodGroup, remainder);
            }
        }
    }

    public static boolean isStockAvailable(int hospitalId, String bloodGroup, int units) throws SQLException {
        try {
            Map<String, Integer> stock = getStockByHospital(hospitalId);
            return stock.getOrDefault(bloodGroup, 0) >= units;
        } catch (Exception e) {
            throw new SQLException("Error checking stock availability", e);
        }
    }
    
    public static void setStock(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, units);
            ps.setInt(4, units);
            ps.executeUpdate();
        }
    }
    
    
 // In file: StockDAO.java
 // Add these two new methods anywhere inside the StockDAO class

     /**
      * ✅ NEW TRANSACTIONAL VERSION: Adds units to the manual ledger using an existing connection.
      */
     public static void addUnits(int hospitalId, String bloodGroup, int units, Connection con) throws SQLException {
         String sql = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE units = units + ?";
         try (PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setInt(1, hospitalId);
             ps.setString(2, bloodGroup);
             ps.setInt(3, units);
             ps.setInt(4, units); // Value for the "ON DUPLICATE KEY UPDATE" part.
             ps.executeUpdate();
         }
     }

     /**
      * ✅ NEW TRANSACTIONAL VERSION: Subtracts units from the manual ledger using an existing connection.
      */
     public static void takeUnits(int hospitalId, String bloodGroup, int units, Connection con) throws SQLException {
         String sql = "UPDATE blood_stock SET units = units - ? WHERE hospital_id = ? AND blood_group = ? AND units >= ?";
         try (PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setInt(1, units);
             ps.setInt(2, hospitalId);
             ps.setString(3, bloodGroup);
             ps.setInt(4, units);
             ps.executeUpdate();
         }
     }
  // ... all your existing methods in StockDAO.java remain unchanged ...

     /**
      * ✅ NEW TRANSACTIONAL METHOD: Decrements the stock count in the summary table.
      * This is designed to be called only after bags are successfully removed from
      * the detailed inventory table to ensure data consistency.
      *
      * @param hospitalId The ID of the hospital.
      * @param bloodGroup The blood group to update.
      * @param unitsToRemove The number of units to decrement.
      * @param con The active database connection for the transaction.
      * @throws SQLException if a database error occurs.
      */
     public static boolean removeStockPrioritizingInventory(int hospitalId, String bloodGroup, int unitsToRemove) throws SQLException {
         Connection con = null;
         try {
             con = DBUtil.getConnection();
             con.setAutoCommit(false); // Start transaction

             // Step 1: Check total available stock first to prevent unnecessary operations
             Map<String, Integer> currentStock = getStockByHospital(hospitalId);
             int totalAvailable = currentStock.getOrDefault(bloodGroup, 0);

             if (totalAvailable < unitsToRemove) {
                 con.rollback(); // Not enough stock, cancel transaction
                 return false;
             }

             // Step 2: Find out how many traceable bags are in the inventory
             int inventoryBagsCount = 0;
             String countSql = "SELECT COUNT(*) FROM blood_inventory WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED'";
             try (PreparedStatement ps = con.prepareStatement(countSql)) {
                 ps.setInt(1, hospitalId);
                 ps.setString(2, bloodGroup);
                 try (ResultSet rs = ps.executeQuery()) {
                     if (rs.next()) {
                         inventoryBagsCount = rs.getInt(1);
                     }
                 }
             }

             // Step 3: Remove from `blood_inventory` first
             int unitsTakenFromInventory = 0;
             if (inventoryBagsCount > 0) {
                 String updateInventorySql = "UPDATE blood_inventory SET inventory_status = 'USED' " +
                                             "WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED' " +
                                             "ORDER BY expiry_date ASC LIMIT ?";
                 try (PreparedStatement ps = con.prepareStatement(updateInventorySql)) {
                     ps.setInt(1, hospitalId);
                     ps.setString(2, bloodGroup);
                     ps.setInt(3, unitsToRemove); // Try to remove all needed units
                     unitsTakenFromInventory = ps.executeUpdate();
                 }
             }

             // Step 4: If more units need to be removed, take them from the manual `blood_stock` ledger
             int remainderToRemove = unitsToRemove - unitsTakenFromInventory;
             if (remainderToRemove > 0) {
                 String updateStockSql = "UPDATE blood_stock SET units = units - ? WHERE hospital_id = ? AND blood_group = ?";
                 try (PreparedStatement ps = con.prepareStatement(updateStockSql)) {
                     ps.setInt(1, remainderToRemove);
                     ps.setInt(2, hospitalId);
                     ps.setString(3, bloodGroup);
                     ps.executeUpdate();
                 }
             }

             con.commit(); // Finalize the transaction
             return true;

         } catch (Exception e) {
             if (con != null) {
                 con.rollback(); // Rollback on any error
             }
             throw new SQLException("Error during stock removal transaction.", e);
         } finally {
             if (con != null) {
                 try {
                     con.setAutoCommit(true);
                     con.close();
                 } catch (SQLException e) {
                     e.printStackTrace();
                 }
             }
         }
     }
}