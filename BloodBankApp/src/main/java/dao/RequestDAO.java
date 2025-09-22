package dao;

import model.Request;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List;
import java.util.Map;   

public class RequestDAO {

    /**
     * Fetches all pending requests that a specific hospital has NOT YET declined.
     */
    public static List<Request> getPendingRequestsForHospital(int hospitalId) throws Exception {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.request_id, u.name, u.contact_number, r.blood_group, r.units_requested, r.request_date " +
                     "FROM requests r JOIN users u ON r.patient_id = u.user_id " +
                     "WHERE r.status = 'PENDING' AND r.request_id NOT IN " +
                     "(SELECT ra.request_id FROM request_actions ra WHERE ra.hospital_id = ?) " +
                     "ORDER BY r.request_date ASC";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, hospitalId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setPatientName(rs.getString("name"));
                    req.setPatientPhone(rs.getString("contact_number"));
                    req.setBloodGroup(rs.getString("blood_group"));
                    req.setUnits(rs.getInt("units_requested"));
                    req.setRequestDate(rs.getDate("request_date"));
                    requests.add(req);
                }
            }
        }
        return requests;
    }

    /**
     * Records that a specific hospital has declined a request.
     */
    public static void declineRequestForHospital(int requestId, int hospitalId) throws Exception {
        String sql = "INSERT INTO request_actions (request_id, hospital_id, action) VALUES (?, ?, 'DECLINED')";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, hospitalId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Updates the global status of a request (e.g., "PENDING" to "FULFILLED")
     */
    public static void updateRequestStatus(int requestId, String status) throws Exception {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    public static Request getRequestById(int requestId) throws Exception {
        String sql = "SELECT * FROM requests WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setBloodGroup(rs.getString("blood_group"));
                    req.setUnits(rs.getInt("units_requested"));
                    return req;
                }
            }
        }
        return null;
    }

    /**
     * ✅ FIXED: For Feature 2 (Analytics).
     * Calculates the average units for approved/fulfilled requests by a hospital.
     */
    public static Map<String, Double> getAverageDailyRequests(int hospitalId) throws SQLException {
        Map<String, Double> avgRequests = new HashMap<>();
        String sql = "SELECT r.blood_group, AVG(r.units_requested) AS avg_units " +
                     "FROM requests r " +
                     "JOIN request_actions ra ON r.request_id = ra.request_id " +
                     "WHERE ra.hospital_id = ? " +
                     "AND (ra.action = 'APPROVED' OR ra.action = 'FULFILLED') " + 
                     "GROUP BY r.blood_group";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, hospitalId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    avgRequests.put(rs.getString("blood_group"), rs.getDouble("avg_units"));
                }
            }
        }
        return avgRequests;
    }

    /**
     * ✅ NEW: For Feature 2 (Analytics).
     * Logs a hospital's action (e.g., "APPROVED") for a request.
     */
    public static void logRequestAction(int requestId, int hospitalId, String action) throws Exception {
        String sql = "INSERT INTO request_actions (request_id, hospital_id, action) VALUES (?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, hospitalId);
            ps.setString(3, action);
            ps.executeUpdate();
        }
    }

    /**
     * ✅ NEW: For Feature 2 (Real-Time Tracking).
     * Updates the patient-facing tracking status of a request.
     */
    public static void updateTrackingStatus(int requestId, String trackingStatus) throws Exception {
        String sql = "UPDATE requests SET tracking_status = ? WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trackingStatus);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }
 // This is the method I will add:
    public static List<Request> getRequestsByPatientId(int patientId) throws Exception {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM requests WHERE patient_id = ? ORDER BY request_date DESC";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, patientId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setBloodGroup(rs.getString("blood_group"));
                    req.setUnits(rs.getInt("units_requested"));
                    req.setRequestDate(rs.getDate("request_date"));
                    req.setStatus(rs.getString("status"));
                    req.setTrackingStatus(rs.getString("tracking_status")); // We'll grab the new status
                    requests.add(req);
                }
            }
        }
        return requests;
    }
}