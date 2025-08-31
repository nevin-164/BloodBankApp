package model;

import java.sql.Date;

public class Request {
    private int requestId;
    private String patientName;
    private String patientPhone; // ✅ ADD THIS
    private String bloodGroup;
    private int units;
    private Date requestDate;
    private String status;

    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    // ✅ ADD THESE TWO METHODS
    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }
    
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}