package dao;

import model.Request;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

	// In RequestDAO.java, replace the getPendingRequests method

	// In your dao/RequestDAO.java file, replace the getPendingRequests method.

	public static java.util.List<Request> getPendingRequests() throws Exception {
	    java.util.List<Request> requests = new java.util.ArrayList<>();
	    // This query joins the requests and users tables to get the patient's name and contact number
	    String sql = "SELECT r.request_id, u.name, u.contact_number, r.blood_group, r.units_requested, r.request_date " +
	                 "FROM requests r JOIN users u ON r.patient_id = u.user_id " +
	                 "WHERE r.status = 'PENDING' ORDER BY r.request_date ASC";
	    
	    try (java.sql.Connection con = DBUtil.getConnection();
	         java.sql.PreparedStatement ps = con.prepareStatement(sql);
	         java.sql.ResultSet rs = ps.executeQuery()) {
	        
	        while (rs.next()) {
	            Request req = new Request();
	            req.setRequestId(rs.getInt("request_id"));
	            req.setPatientName(rs.getString("name"));
	            // ✅ This line requires the setPatientPhone method from the step above
	            req.setPatientPhone(rs.getString("contact_number"));
	            req.setBloodGroup(rs.getString("blood_group"));
	            req.setUnits(rs.getInt("units_requested"));
	            req.setRequestDate(rs.getDate("request_date"));
	            requests.add(req);
	        }
	    }
	    return requests;
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
    
    public static void updateRequestStatus(int requestId, String status) throws Exception {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }
}