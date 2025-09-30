package dao;

import model.Request;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public static List<Request> getAllPendingRequests() throws Exception {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.*, u.name as patientName FROM requests r JOIN users u ON r.patient_id = u.user_id " +
                     "WHERE r.status = 'PENDING' ORDER BY r.request_date ASC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        return requests;
    }

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
                    return req;
                }
            }
        }
        return null;
    }

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
     * ✅ NEWLY ADDED: This method was missing, causing the compile error.
     * It updates a separate tracking_status column, which is intended for patient visibility.
     * NOTE: This assumes you have added a `tracking_status VARCHAR(50)` column to your `requests` table.
     */
    public static void updateTrackingStatus(int requestId, String trackingStatus) throws Exception {
        String sql = "UPDATE requests SET tracking_status = ? WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trackingStatus);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

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
                    requests.add(req);
                }
            }
        }
        return requests;
    }
}