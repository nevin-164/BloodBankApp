package model;

import java.sql.Timestamp;

/**
 * ✅ FINAL, DEFINITIVE FIX: This is the master data model for a blood request.
 * It is now perfectly synchronized with the database and all DAOs/Servlets.
 * This version includes the critical 'trackingStatus' field to prevent runtime
 * errors on the patient dashboard.
 */
public class Request {

    private int requestId;
    private int userId;
    private int hospitalId;
    private String patientName;
    private String bloodGroup;
    private int units;
    private String status;
    private Timestamp createdAt;

    // Extra fields populated by DAO JOINs for easier display
    private String userName; 
    private String hospitalName;
    
    // ✅ CRITICAL FIX: Added the trackingStatus field to match the database and JSP.
    private String trackingStatus;

    /**
     * A standard getter that allows JSPs to access the requestId using the common shorthand `${req.id}`.
     * This is crucial for compatibility with JSP Expression Language (EL).
     * @return The unique ID of the request.
     */
    public int getId() {
        return requestId;
    }

    // --- Standard Getters and Setters for all fields ---
    
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    
    // ✅ CRITICAL FIX: Added getter and setter for trackingStatus.
    public String getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(String trackingStatus) {
        this.trackingStatus = trackingStatus;
    }
}