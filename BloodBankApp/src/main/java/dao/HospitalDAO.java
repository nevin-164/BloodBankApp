package dao;

import model.Hospital;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalDAO {

    // ✅ Create (used by AddHospitalServlet)
    public static void insertHospital(Hospital hospital) throws Exception {
        String sql = "INSERT INTO hospitals (name, email, password, contact_number, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hospital.getName());
            ps.setString(2, hospital.getEmail());
            ps.setString(3, hospital.getPassword());
            ps.setString(4, hospital.getContactNumber());
            ps.setString(5, hospital.getAddress());
            ps.executeUpdate();
        }
    }

    // ✅ Read all (used by HospitalListServlet)
    public static List<Hospital> getAllHospitals() throws Exception {
        List<Hospital> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitals";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Hospital h = new Hospital();
                h.setHospitalId(rs.getInt("hospital_id"));
                h.setName(rs.getString("name"));
                h.setEmail(rs.getString("email"));
                h.setPassword(rs.getString("password"));
                h.setContactNumber(rs.getString("contact_number"));
                h.setAddress(rs.getString("address"));
                list.add(h);
            }
        }
        return list;
    }

    // ✅ Read one by id (used by EditHospitalServlet GET)
    public static Hospital getHospitalById(int id) throws Exception {
        String sql = "SELECT * FROM hospitals WHERE hospital_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Hospital h = new Hospital();
                    h.setHospitalId(rs.getInt("hospital_id"));
                    h.setName(rs.getString("name"));
                    h.setEmail(rs.getString("email"));
                    h.setPassword(rs.getString("password"));
                    h.setContactNumber(rs.getString("contact_number"));
                    h.setAddress(rs.getString("address"));
                    return h;
                }
            }
        }
        return null;
    }

    // ✅ Update (used by EditHospitalServlet POST)
    // Note: Not updating password here; add it if you want to support password changes.
    public static void updateHospital(Hospital hospital) throws Exception {
        String sql = "UPDATE hospitals SET name=?, email=?, contact_number=?, address=? WHERE hospital_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hospital.getName());
            ps.setString(2, hospital.getEmail());
            ps.setString(3, hospital.getContactNumber());
            ps.setString(4, hospital.getAddress());
            ps.setInt(5, hospital.getHospitalId());
            ps.executeUpdate();
        }
    }


    // ✅ Delete (used by DeleteHospitalServlet)
    public static void deleteHospital(int hospitalId) throws Exception {
        String sql = "DELETE FROM hospitals WHERE hospital_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ps.executeUpdate();
        }
    }

    // ✅ Login support for hospitals (if your LoginServlet checks hospitals)
    public static Hospital findByEmailAndPassword(String email, String password) throws Exception {
        String sql = "SELECT * FROM hospitals WHERE email=? AND password=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Hospital h = new Hospital();
                    h.setHospitalId(rs.getInt("hospital_id"));
                    h.setName(rs.getString("name"));
                    h.setEmail(rs.getString("email"));
                    h.setPassword(rs.getString("password"));
                    h.setContactNumber(rs.getString("contact_number"));
                    h.setAddress(rs.getString("address"));
                    return h;
                }
            }
        }
        return null;
    }
}
