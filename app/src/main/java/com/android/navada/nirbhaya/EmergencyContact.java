package com.android.navada.nirbhaya;

/**
 * Created by {navada} on {22/9/18}
 */
public class EmergencyContact {

    String name,email,number;

    public EmergencyContact(String name, String email, String number) {
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }
}
