package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.StockTransfer;

public class StockTransferDAO {

    public static void createTransferRequest(StockTransfer transfer) throws Exception {
        String sql = "INSERT INTO stock_transfers (requesting_hospital_id, supplying_hospital_id, blood_group, units) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transfer.getRequestingHospitalId());
            ps.setInt(2, transfer.getSupplyingHospitalId());
            ps.setString(3, transfer.getBloodGroup());
            ps.setInt(4, transfer.getUnits());
            ps.executeUpdate();
        }
    }

    public static List<StockTransfer> getPendingTransfersForHospital(int supplyingHospitalId) throws Exception {
        List<StockTransfer> transfers = new ArrayList<>();
        String sql = "SELECT st.*, h.name as requesting_hospital_name FROM stock_transfers st " +
                     "JOIN hospitals h ON st.requesting_hospital_id = h.hospital_id " +
                     "WHERE st.supplying_hospital_id = ? AND st.transfer_status = 'PENDING'";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
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

    public static StockTransfer getTransferById(int transferId) throws Exception {
        StockTransfer transfer = null;
        String sql = "SELECT * FROM stock_transfers WHERE transfer_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transferId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    transfer = new StockTransfer();
                    transfer.setTransferId(rs.getInt("transfer_id"));
                    transfer.setRequestingHospitalId(rs.getInt("requesting_hospital_id"));
                    transfer.setSupplyingHospitalId(rs.getInt("supplying_hospital_id"));
                    transfer.setBloodGroup(rs.getString("blood_group"));
                    transfer.setUnits(rs.getInt("units"));
                    transfer.setRequestTimestamp(rs.getTimestamp("request_timestamp"));
                    transfer.setTransferStatus(rs.getString("transfer_status"));
                }
            }
        }
        return transfer;
    }

    public static void updateTransferStatus(int transferId, String newStatus) throws Exception {
        String sql = "UPDATE stock_transfers SET transfer_status = ? WHERE transfer_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, transferId);
            ps.executeUpdate();
        }
    }
}