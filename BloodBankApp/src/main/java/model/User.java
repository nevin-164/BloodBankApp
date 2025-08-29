package model;

import java.sql.Date;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String role;        // DONOR, PATIENT, ADMIN
    private String bloodGroup;
    private Date lastDonationDate;
    private Date nextEligibleDate;

    public User() {}

    public User(int userId, String name, String email, String password, String role, String bloodGroup,
                Date lastDonationDate, Date nextEligibleDate) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.bloodGroup = bloodGroup;
        this.lastDonationDate = lastDonationDate;
        this.nextEligibleDate = nextEligibleDate;
    }

    // --- Getters & Setters ---
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Date getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(Date lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public Date getNextEligibleDate() {
        return nextEligibleDate;
    }

    public void setNextEligibleDate(Date nextEligibleDate) {
        this.nextEligibleDate = nextEligibleDate;
    }

    @Override
    public String toString() {
        return "User {" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", lastDonationDate=" + lastDonationDate +
                ", nextEligibleDate=" + nextEligibleDate +
                '}';
    }

    // Utility: calculate eligibility +90 days
    public Date calculateNextEligibleDate() {
        if (lastDonationDate == null) {
            return null;
        }
        long ms = lastDonationDate.getTime() + (90L * 24 * 60 * 60 * 1000);
        return new Date(ms);
    }
}
