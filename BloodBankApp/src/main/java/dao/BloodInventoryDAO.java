package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.BloodInventory;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing the detailed Blood Inventory.
 * This class contains all methods required for the entire application workflow, including
 * the critical methods to participate in database transactions.
 */
public class BloodInventoryDAO {
	
    /**
     * ✅ OVERLOADED VERSION FOR TRANSACTIONS:
     * Accepts an existing database connection to safely participate in transactions.
     */
	public static void addBag(int donationId, int hospitalId, String bloodGroup, Date dateDonated, Connection con) throws SQLException {
        String sql = "INSERT INTO blood_inventory (donation_id, hospital_id, blood_group, date_donated, expiry_date, inventory_status) VALUES (?, ?, ?, ?, DATE_ADD(?, INTERVAL 42 DAY), 'PENDING_TEST')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            ps.setInt(2, hospitalId);
            ps.setString(3, bloodGroup);
            ps.setDate(4, dateDonated);
            ps.setDate(5, dateDonated); // Used again for the expiry date calculation
            ps.executeUpdate();
        }
    }

    /**
     * ✅ NEW TRANSACTIONAL METHOD: Clears all pending bags for a specific donation.
     * This is designed to be called from within the approveDonationTransaction in DonationDAO.
     *
     * @param donationId The ID of the donation whose bags are to be cleared.
     * @param con The existing database connection for the transaction.
     * @return The number of bags updated.
     * @throws SQLException if a database error occurs.
     */
    public static int clearPendingBagsForDonation(int donationId, Connection con) throws SQLException {
        String sql = "UPDATE blood_inventory SET inventory_status = 'CLEARED' WHERE donation_id = ? AND inventory_status = 'PENDING_TEST'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            return ps.executeUpdate();
        }
    }

    // --- Methods for Standard Donation and Inventory Flow ---

    /**
     * Retrieves a list of all blood bags for a hospital that are awaiting testing.
     */
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

    /**
     * Updates the status of a single blood bag. Used for moving a bag through the workflow.
     */
    public static void updateBagStatus(int bagId, String newStatus) throws Exception {
        String sql = "UPDATE blood_inventory SET inventory_status = ? WHERE bag_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bagId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Finds the oldest available bags of a specific type and updates their status to 'USED'.
     */
    public static int useOldestClearedBags(int hospitalId, String bloodGroup, int unitsToUse) throws Exception {
        String findBagsSql = "SELECT bag_id FROM blood_inventory WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED' ORDER BY date_donated ASC LIMIT ?";
        List<Integer> bagIdsToUse = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement findPs = con.prepareStatement(findBagsSql)) {
            findPs.setInt(1, hospitalId);
            findPs.setString(2, bloodGroup);
            findPs.setInt(3, unitsToUse);
            try (ResultSet rs = findPs.executeQuery()) {
                while (rs.next()) {
                    bagIdsToUse.add(rs.getInt("bag_id"));
                }
            }
        }
        
        if (bagIdsToUse.size() < unitsToUse) { return 0; }

        String updateSql = "UPDATE blood_inventory SET inventory_status = 'USED' WHERE bag_id = ?";
        int updatedCount = 0;
        try (Connection con = DBUtil.getConnection(); PreparedStatement updatePs = con.prepareStatement(updateSql)) {
            for (int bagId : bagIdsToUse) {
                updatePs.setInt(1, bagId);
                updatedCount += updatePs.executeUpdate();
            }
        }
        return updatedCount;
    }

    // --- Methods for Manual Stock Addition & Removal ---

    /**
     * Manually inserts a new, already-cleared blood bag into the inventory.
     */
    public static void manuallyAddClearedBag(int hospitalId, String bloodGroup, int units) throws Exception {
        String sql = "INSERT INTO blood_inventory (hospital_id, blood_group, date_donated, expiry_date, inventory_status, donation_id) " +
                     "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 42 DAY), 'CLEARED', NULL)";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < units; i++) {
                ps.setInt(1, hospitalId);
                ps.setString(2, bloodGroup);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
    /**
     * Manually removes a specified number of the oldest cleared blood bags of a certain type.
     */
    public static int manuallyRemoveClearedBags(int hospitalId, String bloodGroup, int unitsToRemove) throws Exception {
        String sql = "DELETE FROM blood_inventory WHERE hospital_id = ? AND blood_group = ? AND inventory_status = 'CLEARED' ORDER BY date_donated ASC LIMIT ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.setString(2, bloodGroup);
            ps.setInt(3, unitsToRemove);
            return ps.executeUpdate();
        }
    }

    // --- Methods for Inter-Hospital Transfers ---

    /**
     * Gets the current count of cleared, available blood bags for a specific blood group at a hospital.
     */
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

    /**
     * Transfers a specified number of blood bags from one hospital to another.
     */
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

    /**
     * Retrieves a list of all blood bags for a hospital that are currently in transit.
     */
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

    /**
     * This is the critical security check for receiving transfers.
     */
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

    /**
     * Receives all in-transit blood bags for a specific hospital, changing their status to 'CLEARED'.
     */
    public static int receiveAllBagsForHospital(int hospitalId) throws Exception {
        String sql = "UPDATE blood_inventory SET inventory_status = 'CLEARED' WHERE hospital_id = ? AND inventory_status = 'IN_TRANSIT'";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            return ps.executeUpdate(); 
        }
    }
}