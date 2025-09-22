package model;

import java.sql.Date;

public class BloodInventory {

    private int bagId;
    private int donationId;
    private int hospitalId;
    private String bloodGroup;
    private Date dateDonated;
    private Date expiryDate;
    private String inventoryStatus;

    // Getters
    public int getBagId() {
        return bagId;
    }
    
    public int getDonationId() {
        return donationId;
    }
    
    public int getHospitalId() {
        return hospitalId;
    }
    
    public String getBloodGroup() {
        return bloodGroup;
    }
    
    public Date getDateDonated() {
        return dateDonated;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public String getInventoryStatus() {
        return inventoryStatus;
    }

    // Setters
    public void setBagId(int bagId) {
        this.bagId = bagId;
    }
    
    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }
    
    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
    
    public void setDateDonated(Date dateDonated) {
        this.dateDonated = dateDonated;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }
}