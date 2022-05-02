package com.example.propertyproject.models;

public class AppointmentModel {
    String address;
    String contractor;
    String date;
    String description;
    String documentId;

    public AppointmentModel(){

    }

    public AppointmentModel(String address, String contractor, String date, String description, String documentId) {
        this.address = address;
        this.contractor = contractor;
        this.date = date;
        this.description = description;
        this.documentId = documentId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContractor() {
        return contractor;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
