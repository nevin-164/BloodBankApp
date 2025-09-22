package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // ✅ ADDED
import java.util.ArrayList; // ✅ ADDED
import java.util.List; // ✅ ADDED
import model.BloodInventory;

public class BloodInventoryDAO {

    /**
     * Inserts a new blood bag record into the inventory.
     * This will be called by ApproveDonationServlet.
     */
    public static void addBag(BloodInventory bag) throws Exception {
        String sql = "INSERT INTO blood_inventory (donation_id, hospital_id, blood_group, date_donated, expiry_date, inventory_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, bag.getDonationId());
            ps.setInt(2, bag.getHospitalId());
            ps.setString(3, bag.getBloodGroup());
            ps.setDate(4, bag.getDateDonated());
            ps.setDate(5, bag.getExpiryDate());
            ps.setString(6, bag.getInventoryStatus());
            
            ps.executeUpdate();
        }
    }
    
    /**
     * ✅ NEW: Gets all bags for a hospital that are still 'PENDING_TEST'.
     * This will populate the new panel on the hospital dashboard.
     */
    public static List<BloodInventory> getPendingBagsByHospital(int hospitalId) throws Exception {
        List<BloodInventory> pendingBags = new ArrayList<>();
        String sql = "SELECT * FROM blood_inventory " +
                     "WHERE hospital_id = ? AND inventory_status = 'PENDING_TEST' " +
                     "ORDER BY date_donated ASC";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, hospitalId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BloodInventory bag = new BloodInventory();
                    bag.setBagId(rs.getInt("bag_id"));
                    bag.setDonationId(rs.getInt("donation_id"));
                    bag.setBloodGroup(rs.getString("blood_group"));
                    bag.setDateDonated(rs.getDate("date_donated"));
                    bag.setExpiryDate(rs.getDate("expiry_date"));
                    bag.setInventoryStatus(rs.getString("inventory_status"));
                    pendingBags.add(bag);
                }
            }
        }
        return pendingBags;
    }
    
    /**
     * ✅ NEW: A generic method to update a bag's status.
     * This will be called by our new servlet (e.g., to 'CLEARED').
     */
    public static void updateBagStatus(int bagId, String newStatus) throws Exception {
        String sql = "UPDATE blood_inventory SET inventory_status = ? WHERE bag_id = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, newStatus);
            ps.setInt(2, bagId);
            ps.executeUpdate();
        }
    }
}