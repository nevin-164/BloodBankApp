package dao;

import model.Hospital;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalDAO {

    public static void insertHospital(Hospital hospital) throws Exception {
        String insertHospitalSQL = "INSERT INTO hospitals (name, email, password, contact_number, address) VALUES (?, ?, ?, ?, ?)";
        String initStockSQL = "INSERT INTO blood_stock (hospital_id, blood_group, units) VALUES (?, ?, 0)";
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false);
            int newHospitalId = 0;
            try (PreparedStatement psHospital = con.prepareStatement(insertHospitalSQL, Statement.RETURN_GENERATED_KEYS)) {
                psHospital.setString(1, hospital.getName());
                psHospital.setString(2, hospital.getEmail());
                psHospital.setString(3, hospital.getPassword());
                psHospital.setString(4, hospital.getContactNumber());
                psHospital.setString(5, hospital.getAddress());
                psHospital.executeUpdate();
                try (ResultSet generatedKeys = psHospital.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newHospitalId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating hospital failed, no ID obtained.");
                    }
                }
            }
            String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            try (PreparedStatement psStock = con.prepareStatement(initStockSQL)) {
                for (String bg : bloodGroups) {
                    psStock.setInt(1, newHospitalId);
                    psStock.setString(2, bg);
                    psStock.addBatch();
                }
                psStock.executeBatch();
            }
            con.commit();
        } catch (Exception e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /**
     * ✅ DEFINITIVE, FINAL FIX: This method now safely deletes a hospital and ALL of its associated data
     * in the correct order. It handles all foreign key constraints by using the correct column names
     * for all tables, including the corrected `supplying_hospital_id` for stock transfers.
     */
    public static void deleteHospital(int hospitalId) throws Exception {
        // SQL statements for deleting all related records
        String deleteInventoryViaDonationSQL = "DELETE bi FROM blood_inventory bi JOIN donations d ON bi.donation_id = d.donation_id WHERE d.hospital_id = ?";
        String deleteInventoryDirectSQL = "DELETE FROM blood_inventory WHERE hospital_id = ?";
        String deleteStockSQL = "DELETE FROM blood_stock WHERE hospital_id = ?";
        String deleteRequestActionsSQL = "DELETE FROM request_actions WHERE hospital_id = ?";
        String deleteStockTransfersSQL = "DELETE FROM stock_transfers WHERE requesting_hospital_id = ? OR supplying_hospital_id = ?";
        String deleteDonationsSQL = "DELETE FROM donations WHERE hospital_id = ?";
        String deleteHospitalSQL = "DELETE FROM hospitals WHERE hospital_id = ?";
        
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            // Start a transaction
            con.setAutoCommit(false);

            // Step 1: Clear inventory linked via donations
            try (PreparedStatement ps = con.prepareStatement(deleteInventoryViaDonationSQL)) {
                ps.setInt(1, hospitalId);
                ps.executeUpdate();
            }

            // Step 2: Clear inventory linked directly to the hospital
            try (PreparedStatement ps = con.prepareStatement(deleteInventoryDirectSQL)) {
                ps.setInt(1, hospitalId);
                ps.executeUpdate();
            }
            
            // Step 3: Delete from blood_stock
            try (PreparedStatement ps = con.prepareStatement(deleteStockSQL)) {
                ps.setInt(1, hospitalId);
                ps.executeUpdate();
            }
            
            // Step 4: Delete from request_actions
            try (PreparedStatement ps = con.prepareStatement(deleteRequestActionsSQL)) {
                ps.setInt(1, hospitalId);
                ps.executeUpdate();
            }
            
            // Step 5: ✅ CRITICAL FIX - Delete from stock_transfers using the correct column name
            try (PreparedStatement ps = con.prepareStatement(deleteStockTransfersSQL)) {
                ps.setInt(1, hospitalId);
                ps.setInt(2, hospitalId);
                ps.executeUpdate();
            }

            // Step 6: Delete from donations (now safe to do)
            try (PreparedStatement ps = con.prepareStatement(deleteDonationsSQL)) {
                ps.setInt(1, hospitalId);
                ps.executeUpdate();
            }
            
            // Step 7: Finally, delete the hospital itself
            try (PreparedStatement ps = con.prepareStatement(deleteHospitalSQL)) {
                ps.setInt(1, hospitalId);
                ps.executeUpdate();
            }

            // If all deletions were successful, commit the transaction
            con.commit();

        } catch (Exception e) {
            // If any error occurred, roll back the entire transaction
            if (con != null) {
                con.rollback();
            }
            throw e; // Re-throw the exception
        } finally {
            // Always close the connection and restore auto-commit mode
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }
    
    // The rest of your HospitalDAO methods remain the same...

    public static List<Hospital> getAllHospitals() throws Exception {
        List<Hospital> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitals";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Hospital h = new Hospital();
                h.setId(rs.getInt("hospital_id"));
                h.setName(rs.getString("name"));
                h.setEmail(rs.getString("email"));
                h.setContactNumber(rs.getString("contact_number"));
                h.setAddress(rs.getString("address"));
                list.add(h);
            }
        }
        return list;
    }

    public static Hospital getHospitalById(int id) throws Exception {
        String sql = "SELECT * FROM hospitals WHERE hospital_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Hospital h = new Hospital();
                    h.setId(rs.getInt("hospital_id"));
                    h.setName(rs.getString("name"));
                    h.setEmail(rs.getString("email"));
                    h.setContactNumber(rs.getString("contact_number"));
                    h.setAddress(rs.getString("address"));
                    return h;
                }
            }
        }
        return null;
    }

    public static void updateHospital(Hospital hospital) throws Exception {
        String sql = "UPDATE hospitals SET name=?, email=?, contact_number=?, address=? WHERE hospital_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hospital.getName());
            ps.setString(2, hospital.getEmail());
            ps.setString(3, hospital.getContactNumber());
            ps.setString(4, hospital.getAddress());
            ps.setInt(5, hospital.getId());
            ps.executeUpdate();
        }
    }
    
    public static Hospital findByEmailAndPassword(String email, String password) throws Exception {
        String sql = "SELECT * FROM hospitals WHERE email=? AND password=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Hospital h = new Hospital();
                    h.setId(rs.getInt("hospital_id"));
                    h.setName(rs.getString("name"));
                    h.setEmail(rs.getString("email"));
                    return h;
                }
            }
        }
        return null;
    }
}