package model;

import java.sql.Date;

public class Achievement {

    private int achievementId;
    private int userId;
    private String badgeName;
    private String badgeIcon;
    private Date dateEarned;

    // Getters
    public int getAchievementId() {
        return achievementId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getBadgeName() {
        return badgeName;
    }
    
    public String getBadgeIcon() {
        return badgeIcon;
    }
    
    public Date getDateEarned() {
        return dateEarned;
    }

    // Setters
    public void setAchievementId(int achievementId) {
        this.achievementId = achievementId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }
    
    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }
    
    public void setDateEarned(Date dateEarned) {
        this.dateEarned = dateEarned;
    }
}