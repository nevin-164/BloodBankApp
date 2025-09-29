package dao;

import model.Request;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the final, complete, and synchronized version of the RequestDAO.
 * It includes all methods required by other servlets and JSPs with consistent naming,
 * and the SQL syntax has been corrected to match the database schema.
 */
public class RequestDAO {

    /**
     * Fetches all pending requests for a specific hospital.
     */
    public static List<Request> getPendingRequestsForHospital(int hospitalId) throws Exception {
        List<Request> requests = new ArrayList<>();
        // Note: This query assumes the join column is 'patient_id' in requests table
        String sql = "SELECT r.*, u.name as userName FROM requests r JOIN users u ON r.patient_id = u.user_id " +
                     "WHERE r.hospital_id = ? AND r.status = 'PENDING' ORDER BY r.createdAt ASC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("requestId"));
                    req.setPatientName(rs.getString("patientName"));
                    req.setBloodGroup(rs.getString("bloodGroup"));
                    req.setUnits(rs.getInt("units"));
                    req.setStatus(rs.getString("status"));
                    req.setCreatedAt(rs.getTimestamp("createdAt"));
                    req.setUserName(rs.getString("userName"));
                    requests.add(req);
                }
            }
        }
        return requests;
    }

    /**
     * Updates the global status of a request (e.g., "PENDING" to "FULFILLED" or "DECLINED").
     */
    public static void updateRequestStatus(int requestId, String newStatus) throws Exception {
        String sql = "UPDATE requests SET status = ? WHERE requestId = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    /**
     * Fetches a single request by its unique ID.
     */
    public static Request getRequestById(int requestId) throws Exception {
        String sql = "SELECT * FROM requests WHERE requestId = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("requestId"));
                    req.setUserId(rs.getInt("patient_id")); 
                    req.setHospitalId(rs.getInt("hospital_id"));
                    req.setPatientName(rs.getString("patientName"));
                    req.setBloodGroup(rs.getString("bloodGroup"));
                    req.setUnits(rs.getInt("units"));
                    req.setStatus(rs.getString("status"));
                    req.setCreatedAt(rs.getTimestamp("createdAt"));
                    return req;
                }
            }
        }
        return null;
    }
 
    /**
     * Logs a hospital's action for a request.
     */
    public static void logRequestAction(int requestId, int hospitalId, String action) throws Exception {
        String sql = "INSERT INTO request_actions (request_id, hospital_id, action) VALUES (?, ?, ?)";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, hospitalId);
            ps.setString(3, action);
            ps.executeUpdate();
        }
    }

    /**
     * Updates the patient-facing tracking status of a request.
     */
    public static void updateTrackingStatus(int requestId, String trackingStatus) throws Exception {
        String sql = "UPDATE requests SET tracking_status = ? WHERE requestId = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trackingStatus);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    /**
     * Fetches all historical requests made by a specific user.
     */
    public static List<Request> getRequestsByUserId(int userId) throws Exception {
        List<Request> requests = new ArrayList<>();
        // ✅ FIXED: Changed the incorrect column name 'r.hospitalId' to 'r.hospital_id' to match the database schema.
        String sql = "SELECT r.*, h.name as hospitalName FROM requests r " +
                     "LEFT JOIN hospitals h ON r.hospital_id = h.hospital_id " +
                     "WHERE r.patient_id = ? ORDER BY r.createdAt DESC";
        
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("requestId"));
                    req.setBloodGroup(rs.getString("bloodGroup"));
                    req.setUnits(rs.getInt("units"));
                    req.setCreatedAt(rs.getTimestamp("createdAt"));
                    req.setStatus(rs.getString("status"));
                    req.setHospitalName(rs.getString("hospitalName"));
                    requests.add(req);
                }
            }
        }
        return requests;
    }
}

