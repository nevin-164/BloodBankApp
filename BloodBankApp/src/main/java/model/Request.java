package model;

import java.sql.Timestamp;

/**
 * This is the final, definitive data model for a blood request.
 * It serves as the master "blueprint" for a request object. This version
 * is perfectly synchronized with the final RequestDAO and the corrected JSP pages,
 * containing all necessary fields and methods to prevent compilation and runtime errors.
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

    // Extra fields populated by DAO JOINs for easier display in JSPs
    private String userName; // The name of the user (from the 'users' table) who created the request
    private String hospitalName; // The name of the hospital handling the request

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
}

