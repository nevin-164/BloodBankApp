package model;

import java.sql.Timestamp;

public class StockTransfer {

    private int transferId;
    private int requestingHospitalId;
    private int supplyingHospitalId;
    private String bloodGroup;
    private int units;
    private Timestamp requestTimestamp;
    private String transferStatus;

    // Optional: For displaying names in the frontend
    private String requestingHospitalName;
    private String supplyingHospitalName;

    // Getters and Setters
    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getRequestingHospitalId() {
        return requestingHospitalId;
    }

    public void setRequestingHospitalId(int requestingHospitalId) {
        this.requestingHospitalId = requestingHospitalId;
    }

    public int getSupplyingHospitalId() {
        return supplyingHospitalId;
    }

    public void setSupplyingHospitalId(int supplyingHospitalId) {
        this.supplyingHospitalId = supplyingHospitalId;
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

    public Timestamp getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(Timestamp requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getRequestingHospitalName() {
        return requestingHospitalName;
    }

    public void setRequestingHospitalName(String requestingHospitalName) {
        this.requestingHospitalName = requestingHospitalName;
    }

    public String getSupplyingHospitalName() {
        return supplyingHospitalName;
    }

    public void setSupplyingHospitalName(String supplyingHospitalName) {
        this.supplyingHospitalName = supplyingHospitalName;
    }
}