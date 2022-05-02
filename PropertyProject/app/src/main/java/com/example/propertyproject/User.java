package com.example.propertyproject;

public class User {
    public String fullName, loginEmailAddress, loginPassword, phoneNumber;

    public User (String mfullName, String mloginEmailAddress, String mloginPassword, String mphoneNumber )
    {
        this.fullName = mfullName;
        this.loginEmailAddress = mloginEmailAddress;
        this.loginPassword = mloginPassword;
        this.phoneNumber = mphoneNumber;
    }
}
