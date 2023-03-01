package com.adventure.tripplanner;

//model class for traveler data

public class TravelerProfileModel {
    private String name;
    private String mobile;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public TravelerProfileModel(String name, String mobile, String address) {
        this.name = name;
        this.mobile = mobile;
        this.address = address;
    }

    public TravelerProfileModel() { }//default
}
