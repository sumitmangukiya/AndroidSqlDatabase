package com.cysm.androidsqldatabase.model;

import java.io.Serializable;

public class Contacts implements Serializable {
    public int id;
    public String name;
    public String phoneNumber;
    public String image;

    public Contacts(int id, String name, String phoneNumber, String image) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }

    public Contacts(String name, String phoneNumber, String image) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
