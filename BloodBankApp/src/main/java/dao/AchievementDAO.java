package dao;

import model.Achievement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ FINAL VERSION: The definitive Data Access Object for managing Achievements.
 * This version includes new methods to support transactional operations,
 * ensuring that achievements are only awarded when the parent database
 * action (like approving a donation) is successful.
 */
public class AchievementDAO {

    // --- Existing Methods (No Changes) ---

    public static List<Achievement> getAchievementsForUser(int userId) throws SQLException {
        List<Achievement> achievements = new ArrayList<>();
        String sql = "SELECT * FROM achievements WHERE user_id = ? ORDER BY date_earned DESC";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Achievement ach = new Achievement();
                    ach.setAchievementId(rs.getInt("achievement_id"));
                    ach.setUserId(rs.getInt("user_id"));
                    ach.setBadgeName(rs.getString("badge_name"));
                    ach.setBadgeIcon(rs.getString("badge_icon"));
                    ach.setDateEarned(rs.getDate("date_earned"));
                    achievements.add(ach);
                }
            }
        }
        return achievements;
    }
    
    public static boolean hasAchievement(int userId, String badgeName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements WHERE user_id = ? AND badge_name = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, badgeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public static void addAchievement(int userId, String badgeName, String badgeIcon) throws SQLException {
        String sql = "INSERT INTO achievements (user_id, badge_name, badge_icon, date_earned) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, badgeName);
            ps.setString(3, badgeIcon);
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        }
    }

    // --- ✅ NEW: Transaction-Aware Methods ---

    /**
     * Checks if a user has a specific achievement within an existing transaction.
     * This method is crucial for ensuring data consistency when awarding badges.
     * @param userId The ID of the user.
     * @param badgeName The name of the badge to check for.
     * @param con The active database connection for the transaction.
     * @return true if the user has the achievement, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    public static boolean hasAchievement(int userId, String badgeName, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements WHERE user_id = ? AND badge_name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, badgeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Adds a new achievement to a user within an existing transaction.
     * This method ensures the achievement is only saved if the entire transaction commits.
     * @param userId The ID of the user.
     * @param badgeName The name of the badge to award.
     * @param badgeIcon The path to the badge's icon.
     * @param con The active database connection for the transaction.
     * @throws SQLException if a database error occurs.
     */
    public static void addAchievement(int userId, String badgeName, String badgeIcon, Connection con) throws SQLException {
        String sql = "INSERT INTO achievements (user_id, badge_name, badge_icon, date_earned) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, badgeName);
            ps.setString(3, badgeIcon);
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        }
    }
}