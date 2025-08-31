package dao;

import model.Request;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    /**
     * ✅ ADDED: This new method fetches all pending requests that a specific hospital has NOT YET declined.
     */
    public static List<Request> getPendingRequestsForHospital(int hospitalId) throws Exception {
        List<Request> requests = new ArrayList<>();
        // This SQL query selects requests that are 'PENDING' AND do NOT have a 'DECLINED' entry
        // in the 'request_actions' table for the currently logged-in hospital.
        String sql = "SELECT r.request_id, u.name, u.contact_number, r.blood_group, r.units_requested, r.request_date " +
                     "FROM requests r JOIN users u ON r.patient_id = u.user_id " +
                     "WHERE r.status = 'PENDING' AND r.request_id NOT IN " +
                     "(SELECT ra.request_id FROM request_actions ra WHERE ra.hospital_id = ?) " +
                     "ORDER BY r.request_date ASC";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, hospitalId); // Filter out requests already declined by this hospital
            
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
     * This method records that a specific hospital has declined a request.
     * It does NOT change the global status of the request itself.
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
    
    // This method remains the same, as approving a request IS a global action.
    public static void updateRequestStatus(int requestId, String status) throws Exception {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    // This method also remains the same.
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
}