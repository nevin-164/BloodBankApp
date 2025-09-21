package dao;  // <-- Makes sure it's in the 'dao' package

import model.Achievement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.DBUtil; // <-- Includes the database connection

public class AchievementDAO {

    /**
     * Fetches a list of all achievements earned by a specific user.
     */
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
    
    /**
     * A helper method to check if a user already has a specific badge.
     * This prevents us from awarding the "First Donation" badge multiple times.
     */
    public static boolean hasAchievement(int userId, String badgeName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements WHERE user_id = ? AND badge_name = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, badgeName);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Returns true if a record exists
                }
            }
        }
        return false;
    }
    
    /**
     * A helper method to add a new achievement to the database.
     */
    public static void addAchievement(int userId, String badgeName, String badgeIcon) throws SQLException {
        String sql = "INSERT INTO achievements (user_id, badge_name, badge_icon, date_earned) VALUES (?, ?, ?, ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, badgeName);
            ps.setString(3, badgeIcon);
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis())); // Sets date_earned to today
            
            ps.executeUpdate();
        }
    }
}