package com.android.navada.nirbhaya;

/**
 * Created by {navada} on {22/9/18}
 */
public class User {

    private String name,email,phoneNumber;
    private double latitude,longitude;

    public User(String name, String email, String phoneNumber, double latitude, double longitude) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
