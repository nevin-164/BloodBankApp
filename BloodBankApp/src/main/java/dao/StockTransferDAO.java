package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.StockTransfer;

/**
 * ✅ DEFINITIVE FINAL VERSION: Handles all stock transfer logic correctly.
 * This version fixes the critical bug where new transfer requests were not assigned a
 * 'PENDING' status, which prevented them from appearing on the supplier's dashboard.
 */
public class StockTransferDAO {

    /**
     * ✅ CRITICAL FIX: The INSERT statement now correctly includes the 'transfer_status' column,
     * setting it to 'PENDING' by default. This ensures new requests appear correctly.
     */
    public static void createTransferRequest(StockTransfer transfer) throws Exception {
        String sql = "INSERT INTO stock_transfers (requesting_hospital_id, supplying_hospital_id, blood_group, units, transfer_status) VALUES (?, ?, ?, ?, 'PENDING')";
        try (Connection con = DBUtil.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transfer.getRequestingHospitalId());
            ps.setInt(2, transfer.getSupplyingHospitalId());
            ps.setString(3, transfer.getBloodGroup());
            ps.setInt(4, transfer.getUnits());
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves all pending transfer requests for a specific supplying hospital.
     */
    public static List<StockTransfer> getPendingTransfersForHospital(int supplyingHospitalId) throws Exception {
        List<StockTransfer> transfers = new ArrayList<>();
        String sql = "SELECT st.*, h.name as requesting_hospital_name FROM stock_transfers st " +
                     "JOIN hospitals h ON st.requesting_hospital_id = h.hospital_id " +
                     "WHERE st.supplying_hospital_id = ? AND st.transfer_status = 'PENDING'";
        try (Connection con = DBUtil.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supplyingHospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockTransfer transfer = new StockTransfer();
                    transfer.setTransferId(rs.getInt("transfer_id"));
                    transfer.setRequestingHospitalId(rs.getInt("requesting_hospital_id"));
                    transfer.setSupplyingHospitalId(rs.getInt("supplying_hospital_id"));
                    transfer.setBloodGroup(rs.getString("blood_group"));
                    transfer.setUnits(rs.getInt("units"));
                    transfer.setRequestTimestamp(rs.getTimestamp("request_timestamp"));
                    transfer.setTransferStatus(rs.getString("transfer_status"));
                    transfer.setRequestingHospitalName(rs.getString("requesting_hospital_name"));
                    transfers.add(transfer);
                }
            }
        }
        return transfers;
    }

    /**
     * Retrieves a single stock transfer by its ID.
     */
    public static StockTransfer getTransferById(int transferId) throws Exception {
        try(Connection con = DBUtil.getConnection()){
            return getTransferById(transferId, con);
        }
    }
    
    /**
     * Updates the status of a transfer request (e.g., to 'APPROVED' or 'DECLINED').
     */
    public static void updateTransferStatus(int transferId, String newStatus) throws Exception {
        try(Connection con = DBUtil.getConnection()) {
            updateTransferStatus(transferId, newStatus, con);
        }
    }

    // --- All transactional methods are included below for completeness ---

    public static void approveTransferTransaction(int transferId, int supplyingHospitalId) throws SQLException {
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false);

            StockTransfer transfer = getTransferById(transferId, con);
            if (transfer == null) {
                throw new SQLException("Transfer request not found.");
            }
            if (transfer.getSupplyingHospitalId() != supplyingHospitalId) {
                throw new SQLException("You are not authorized to approve this transfer.");
            }

            String bloodGroup = transfer.getBloodGroup();
            int unitsRequested = transfer.getUnits();
            int requestingHospitalId = transfer.getRequestingHospitalId();

            if (!StockDAO.isStockAvailable(supplyingHospitalId, bloodGroup, unitsRequested)) {
                throw new SQLException("Insufficient total stock to approve this transfer.");
            }

            int traceableBagsAvailable = BloodInventoryDAO.getClearedBagCount(supplyingHospitalId, bloodGroup, con);
            int bagsToPhysicallyTransfer = Math.min(unitsRequested, traceableBagsAvailable);

            if (bagsToPhysicallyTransfer > 0) {
                BloodInventoryDAO.transferBags(supplyingHospitalId, requestingHospitalId, bloodGroup, bagsToPhysicallyTransfer, con);
            }

            int remainder = unitsRequested - bagsToPhysicallyTransfer;
            if (remainder > 0) {
                StockDAO.takeUnits(supplyingHospitalId, bloodGroup, remainder, con);
                BloodInventoryDAO.createInTransitBags(requestingHospitalId, bloodGroup, remainder, con);
            }

            updateTransferStatus(transferId, "APPROVED", con);
            con.commit();

        } catch (SQLException e) {
            if (con != null) {
                con.rollback();
            }
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public static StockTransfer getTransferById(int transferId, Connection con) throws SQLException {
        String sql = "SELECT * FROM stock_transfers WHERE transfer_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transferId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StockTransfer st = new StockTransfer();
                    st.setTransferId(rs.getInt("transfer_id"));
                    st.setRequestingHospitalId(rs.getInt("requesting_hospital_id"));
                    st.setSupplyingHospitalId(rs.getInt("supplying_hospital_id"));
                    st.setBloodGroup(rs.getString("blood_group"));
                    st.setUnits(rs.getInt("units"));
                    st.setTransferStatus(rs.getString("transfer_status"));
                    return st;
                }
            }
        }
        return null;
    }

    public static void updateTransferStatus(int transferId, String status, Connection con) throws SQLException {
        String sql = "UPDATE stock_transfers SET transfer_status = ? WHERE transfer_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, transferId);
            ps.executeUpdate();
        }
    }
}