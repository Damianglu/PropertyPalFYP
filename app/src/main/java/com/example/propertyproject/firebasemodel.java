package com.example.propertyproject;

public class firebasemodel {
    String propertyImage;
    String eircode;
    String address;
    String beds;
    String baths;
    String rent;
    String tenantName;
    String tenantEmail;
    String tenantPhone;
    String propertyID;
    public firebasemodel() {

    }

    public firebasemodel(String propertyImage, String eircode, String address, String beds, String baths, String rent, String tenantName, String tenantEmail, String tenantPhone, String propertyID) {
        this.propertyImage = propertyImage;
        this.eircode = eircode;
        this.address = address;
        this.beds = beds;
        this.baths = baths;
        this.rent = rent;
        this.tenantName = tenantName;
        this.tenantEmail = tenantEmail;
        this.tenantPhone = tenantPhone;
        this.propertyID = propertyID;
    }

    public String getPropertyImage() {
        return propertyImage;
    }

    public void setPropertyImage(String propertyImage) {
        this.propertyImage = propertyImage;
    }

    public String getEircode() {
        return eircode;
    }

    public void setEircode(String eircode) {
        this.eircode = eircode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getBaths() {
        return baths;
    }

    public void setBaths(String baths) {
        this.baths = baths;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantEmail() {
        return tenantEmail;
    }

    public void setTenantEmail(String tenantEmail) {
        this.tenantEmail = tenantEmail;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }
}