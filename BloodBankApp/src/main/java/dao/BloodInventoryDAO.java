package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.BloodInventory;

/**
 * This is the final, definitive version of the BloodInventoryDAO.
 * It contains all methods required for the entire application workflow,
 * including manual addition and removal of stock.
 */
public class BloodInventoryDAO {

    // --- Methods for Standard Donation and Inventory Flow ---

    public static void addBag(BloodInventory bag) throws Exception {
        String sql = "INSERT INTO blood_inventory (donation_id, hospital_id, blood_group, date_donated, expiry_date, inventory_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bag.getDonationId());
            ps.setInt(2, bag.getHospitalId());
            ps.setString(3, bag.getBloodGroup());
            ps.setDate(4, bag.getDateDonated());
            ps.setDate(5, bag.getExpiryDate());
            ps.setString(6, bag.getInventoryStatus());
            ps.executeUpdate();
        }
    }

    public static List<BloodInventory> getPendingBagsByHospital(int hospitalId) throws Exception {
        List<BloodInventory> pendingBags = new ArrayList<>();
        String sql = "SELECT * FROM blood_inventory WHERE hospital_id = ? AND inventory_status = 'PENDING_TEST' ORDER BY date_donated ASC";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
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

    public static void updateBagStatus(int bagId, String newStatus) throws Exception {
        String sql = "UPDATE blood_inventory SET inventory_status = ? WHERE bag_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bagId);
            ps.executeUpdate();
        }
    }

    // --- Methods for Manual Stock Addition & Removal ---

    public static void manuallyAddClearedBag(int hospitalId, String bloodGroup) throws Exception {
        String sql = "INSERT INTO blood_inventory (hospital_id, blood_group, date_donated, expiry_date, inventory_status, donation_id) " +
                     "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 42 DAY), 'CLEARED', NULL)";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.executeUpdate();
        }
    }
    
    /**
     * ✅ Method to support the manual stock removal feature.
     * Deletes the oldest 'CLEARED' bags of a specific type for a hospital.
     * @return The number of bags actually deleted.
     */
    public static int manuallyRemoveClearedBags(int hospitalId, String bloodGroup, int unitsToRemove) throws Exception {
        String sql = "DELETE FROM blood_inventory WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED' ORDER BY date_donated ASC LIMIT ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, unitsToRemove);
            return ps.executeUpdate(); // Returns the number of rows affected.
        }
    }

    // --- Methods for Inter-Hospital Transfers ---

    public static int getClearedBagCount(int hospitalId, String bloodGroup) throws Exception {
        String sql = "SELECT COUNT(*) FROM blood_inventory WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED'";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public static int transferBags(int fromHospitalId, int toHospitalId, String bloodGroup, int units) throws Exception {
        String findBagsSql = "SELECT bag_id FROM blood_inventory WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED' ORDER BY date_donated ASC LIMIT ?";
        String updateBagSql = "UPDATE blood_inventory SET hospital_id = ?, inventory_status = 'IN_TRANSIT' WHERE bag_id = ?";
        List<Integer> bagIdsToTransfer = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement findPs = con.prepareStatement(findBagsSql)) {
            findPs.setInt(1, fromHospitalId);
            findPs.setString(2, bloodGroup);
            findPs.setInt(3, units);
            try (ResultSet rs = findPs.executeQuery()) {
                while (rs.next()) {
                    bagIdsToTransfer.add(rs.getInt("bag_id"));
                }
            }
            if (bagIdsToTransfer.isEmpty()) return 0;
            try (PreparedStatement updatePs = con.prepareStatement(updateBagSql)) {
                for (int bagId : bagIdsToTransfer) {
                    updatePs.setInt(1, toHospitalId);
                    updatePs.setInt(2, bagId);
                    updatePs.addBatch();
                }
                int[] updateCounts = updatePs.executeBatch();
                return updateCounts.length;
            }
        }
    }

    public static List<BloodInventory> getInTransitBagsByHospital(int hospitalId) throws Exception {
        List<BloodInventory> inTransitBags = new ArrayList<>();
        String sql = "SELECT * FROM blood_inventory WHERE hospital_id = ? AND inventory_status = 'IN_TRANSIT'";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BloodInventory bag = new BloodInventory();
                    bag.setBagId(rs.getInt("bag_id"));
                    bag.setDonationId(rs.getInt("donation_id"));
                    bag.setBloodGroup(rs.getString("blood_group"));
                    bag.setDateDonated(rs.getDate("date_donated"));
                    inTransitBags.add(bag);
                }
            }
        }
        return inTransitBags;
    }

    public static int getHospitalIdForBag(int bagId) throws Exception {
        String sql = "SELECT hospital_id FROM blood_inventory WHERE bag_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bagId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("hospital_id");
                }
            }
        }
        return -1; // Return -1 if bag not found
    }

    public static int receiveAllBagsForHospital(int hospitalId) throws Exception {
        String sql = "UPDATE blood_inventory SET inventory_status = 'CLEARED' WHERE hospital_id = ? AND inventory_status = 'IN_TRANSIT'";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            return ps.executeUpdate(); 
        }
    }
}

