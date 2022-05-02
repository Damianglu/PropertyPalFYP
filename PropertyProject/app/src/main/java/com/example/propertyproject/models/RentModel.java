package com.example.propertyproject.models;

import android.location.Address;

public class RentModel {
    String propertyImage;
    String eircode;
    String address;
    String beds;
    String baths;
    String rent;
    String tenantName;
    String tenantEmail;
    String tenantPhone;


    public RentModel(){

    }

    public RentModel(String propertyImage, String eircode, String address, String beds, String baths, String rent, String tenantName, String tenantEmail, String tenantPhone) {
        this.propertyImage = propertyImage;
        this.eircode = eircode;
        this.address = address;
        this.beds = beds;
        this.baths = baths;
        this.rent = rent;
        this.tenantName = tenantName;
        this.tenantEmail = tenantEmail;
        this.tenantPhone = tenantPhone;
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
}
