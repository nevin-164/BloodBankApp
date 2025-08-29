package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockDAO {

    public static void addUnits(String bloodGroup, int units) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO blood_stock(blood_group, units) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE units = units + VALUES(units)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bloodGroup);
            ps.setInt(2, units);
            ps.executeUpdate();
        }
    }

    public static boolean takeUnits(String bloodGroup, int units) throws SQLException, ClassNotFoundException {
        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);
            try {
                int available = 0;
                try (PreparedStatement get = con.prepareStatement("SELECT units FROM blood_stock WHERE blood_group = ? FOR UPDATE")) {
                    get.setString(1, bloodGroup);
                    try (ResultSet rs = get.executeQuery()) {
                        if (rs.next()) {
                            available = rs.getInt(1);
                        }
                    }
                }
                if (available < units) {
                    con.rollback();
                    return false;
                }
                try (PreparedStatement upd = con.prepareStatement("UPDATE blood_stock SET units = units - ? WHERE blood_group = ?")) {
                    upd.setInt(1, units);
                    upd.setString(2, bloodGroup);
                    upd.executeUpdate();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
