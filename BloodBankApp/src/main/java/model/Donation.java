package model;

import java.sql.Date;

public class Donation {
    private int donationId;
    private int userId;
    private String donorName;     // For display on hospital dashboard
    private int hospitalId;
    private String hospitalName;  // For display on donor dashboard
    private String bloodGroup;
    private int units;
    private Date donationDate;
    private Date appointmentDate;
    private String status;

    // --- Getters & Setters ---

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
    public String getDonorName() {
        return donorName;
    }
    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }
    public int getHospitalId() {
        return hospitalId;
    }
    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }
    public String getHospitalName() {
        return hospitalName;
    }
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    public String getBloodGroup() {
        return bloodGroup;
    }
    public void setBloodGroup(String bloodGroup) {
        // ✅ FIXED: Corrected the typo from 'bloodGrup' to 'bloodGroup'
        this.bloodGroup = bloodGroup;
    }
    public int getUnits() {
        return units;
    }
    public void setUnits(int units) {
        this.units = units;
    }
    public Date getDonationDate() {
        return donationDate;
    }
    public void setDonationDate(Date donationDate) {
        this.donationDate = donationDate;
    }
    public Date getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}