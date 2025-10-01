package dao;

import model.Request;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ FINAL VERSION: The definitive DAO for managing patient blood requests.
 * This version is fully updated to handle all features correctly.
 */
public class RequestDAO {

    public static List<Request> getAllPendingRequests(int hospitalId) throws Exception {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.request_id, r.blood_group, r.units_requested, r.status, r.request_date, u.name as patientName " +
                     "FROM requests r JOIN users u ON r.patient_id = u.user_id " +
                     "WHERE r.status = 'PENDING' AND r.request_id NOT IN " +
                     "(SELECT ra.request_id FROM request_actions ra WHERE ra.hospital_id = ? AND ra.action = 'DECLINED')";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setPatientName(rs.getString("patientName"));
                    req.setBloodGroup(rs.getString("blood_group"));
                    req.setUnits(rs.getInt("units_requested"));
                    req.setStatus(rs.getString("status"));
                    req.setCreatedAt(rs.getTimestamp("request_date"));
                    requests.add(req);
                }
            }
        }
        return requests;
    }

    public static void checkAndFinalizeRequestStatus(int requestId) throws Exception {
        String countHospitalsSql = "SELECT COUNT(*) FROM hospitals";
        String countDeclinesSql = "SELECT COUNT(*) FROM request_actions WHERE request_id = ? AND action = 'DECLINED'";
        
        try (Connection con = DBUtil.getConnection()) {
            int totalHospitals = 0;
            try (PreparedStatement ps = con.prepareStatement(countHospitalsSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalHospitals = rs.getInt(1);
                }
            }

            int declineCount = 0;
            try (PreparedStatement ps = con.prepareStatement(countDeclinesSql)) {
                ps.setInt(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        declineCount = rs.getInt(1);
                    }
                }
            }

            if (totalHospitals > 0 && declineCount >= totalHospitals) {
                updateRequestStatus(requestId, "DECLINED");
                updateTrackingStatus(requestId, "Unfortunately, all hospitals were unable to fulfill your request at this time.");
            }
        }
    }
    
    /**
     * ✅ FINAL FIX: Retrieves all requests for a specific user and now includes the 'tracking_status'.
     */
    public static List<Request> getRequestsByUserId(int userId) throws Exception {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.*, h.name as hospitalName FROM requests r " +
                     "LEFT JOIN request_actions ra ON r.request_id = ra.request_id AND ra.action = 'APPROVED' " +
                     "LEFT JOIN hospitals h ON ra.hospital_id = h.hospital_id " +
                     "WHERE r.patient_id = ? ORDER BY r.request_date DESC";
        
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setBloodGroup(rs.getString("blood_group"));
                    req.setUnits(rs.getInt("units_requested"));
                    req.setCreatedAt(rs.getTimestamp("request_date"));
                    req.setStatus(rs.getString("status"));
                    req.setHospitalName(rs.getString("hospitalName")); 
                    
                    // ✅ CRITICAL FIX: Read the trackingStatus from the database
                    req.setTrackingStatus(rs.getString("tracking_status"));
                    
                    requests.add(req);
                }
            }
        }
        return requests;
    }

    // No changes needed for the methods below this line
    
    public static void updateRequestStatus(int requestId, String newStatus) throws Exception {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }
    
    public static Request getRequestById(int requestId) throws Exception {
        String sql = "SELECT * FROM requests WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setUserId(rs.getInt("patient_id"));
                    req.setBloodGroup(rs.getString("blood_group"));
                    req.setUnits(rs.getInt("units_requested"));
                    req.setStatus(rs.getString("status"));
                    req.setCreatedAt(rs.getTimestamp("request_date"));
                    // Also fetch trackingStatus here for completeness in other parts of the app
                    req.setTrackingStatus(rs.getString("tracking_status"));
                    return req;
                }
            }
        }
        return null;
    }
    
    public static void logRequestAction(int requestId, int hospitalId, String action) throws Exception {
        String sql = "INSERT IGNORE INTO request_actions (request_id, hospital_id, action) VALUES (?, ?, ?)";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, hospitalId);
            ps.setString(3, action);
            ps.executeUpdate();
        }
    }

    public static void updateTrackingStatus(int requestId, String trackingStatus) throws Exception {
        String sql = "UPDATE requests SET tracking_status = ? WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trackingStatus);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }
}