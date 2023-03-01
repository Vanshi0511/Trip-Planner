package com.adventure.tripplanner;


// choice model class for set and get the status of user (organizer , traveler , not_defined)

public class ChoiceModel
{
    private String value;

    public ChoiceModel(){ } //default constructor

    public String getValue() {
        return value;
    }

    public ChoiceModel(String value) {
        this.value = value;
    } //parametrized constructor

    public void setValue(String value) {
        this.value = value;
    }
}
