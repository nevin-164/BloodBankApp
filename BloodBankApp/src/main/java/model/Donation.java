package model;

import java.sql.Date;

public class Donation {
    private int donationId;
    private int userId;
    private int units;
    private String bloodGroup;
    private Date donationDate;
    private Date expiryDate;
    private boolean testedSafe;

    // Getters and setters
    public int getDonationId() {
        return donationId;
    }
    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public int getUnits() {
        return units;
    }
    public void setUnits(int units) {
        this.units = units;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Date getDonationDate() {
        return donationDate;
    }
    public void setDonationDate(Date donationDate) {
        this.donationDate = donationDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isTestedSafe() {
        return testedSafe;
    }
    public void setTestedSafe(boolean testedSafe) {
        this.testedSafe = testedSafe;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "donationId=" + donationId +
                ", userId=" + userId +
                ", units=" + units +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", donationDate=" + donationDate +
                ", expiryDate=" + expiryDate +
                ", testedSafe=" + testedSafe +
                '}';
    }
}
