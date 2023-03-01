package com.adventure.tripplanner;

//model class for new trip.
public class NewTripModelClass {

    private String destination;
    private String from;
    private String dateFrom;
    private String dateTo;
    private String pickupLocation;
    private String time;
    private String charge;
    private String vehicle;
    private String seats;
    private String inclusions;
    private String description;

    public NewTripModelClass(String destination, String from, String dateFrom, String dateTo, String pickupLocation, String time, String charge, String vehicle, String seats, String inclusions, String description) {
        this.destination = destination;
        this.from = from;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.pickupLocation = pickupLocation;
        this.time = time;
        this.charge = charge;
        this.vehicle = vehicle;
        this.seats = seats;
        this.inclusions = inclusions;
        this.description = description;
    }
    public NewTripModelClass(){}

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getInclusions() {
        return inclusions;
    }

    public void setInclusions(String inclusions) {
        this.inclusions = inclusions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
