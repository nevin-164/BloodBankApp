package model;

public class Hospital {
    private int hospitalId;
    private String name;
    private String email;
    private String password;
    private String contactNumber;
    private String address;

    public Hospital() {}

    public Hospital(int hospitalId, String name, String email, String password, String contactNumber, String address) {
        this.hospitalId = hospitalId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    // Getters & setters
    public int getHospitalId() { return hospitalId; }
    public void setHospitalId(int hospitalId) { this.hospitalId = hospitalId; }

    // ✅ Alias getter/setter for JSP compatibility
    public int getId() { return hospitalId; }
    public void setId(int id) { this.hospitalId = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // 🔥 Alias methods to support JSPs using phone
    public String getPhone() {
        return contactNumber;
    }

    public void setPhone(String phone) {
        this.contactNumber = phone;
    }
}
