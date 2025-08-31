package model;

import java.sql.Date;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String bloodGroup;
    private String contactNumber; // ✅ ADD THIS
    private Date lastDonationDate;
    private Date nextEligibleDate;

    // --- Getters & Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    
    // ✅ ADD THESE METHODS
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    
    public Date getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(Date lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    public Date getNextEligibleDate() { return nextEligibleDate; }
    public void setNextEligibleDate(Date nextEligibleDate) { this.nextEligibleDate = nextEligibleDate; }
}