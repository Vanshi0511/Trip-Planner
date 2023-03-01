package com.adventure.tripplanner;

//model class for organizer data

public class OrganizerProfileModel
{
    private String name;
    private String email;
    private String mobileNo;
    private String teamName;
    private String description;
    private String address;

    public OrganizerProfileModel(String name, String email, String mobileNo, String teamName, String description, String address) {
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.teamName = teamName;
        this.description = description;
        this.address = address;
    }
    public OrganizerProfileModel(){ } // default

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
