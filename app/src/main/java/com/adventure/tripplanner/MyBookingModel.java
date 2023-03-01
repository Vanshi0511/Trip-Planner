package com.adventure.tripplanner;

//model class for viewing travelers bookings till now.
public class MyBookingModel {
    private String teamName;
    private String tripName;
    private String seatBooked;
    private String bookingDate;
    private String amount;

    public MyBookingModel(String teamName, String tripName, String seatBooked, String bookingDate, String amount) {
        this.teamName = teamName;
        this.tripName = tripName;
        this.seatBooked = seatBooked;
        this.bookingDate = bookingDate;
        this.amount = amount;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getSeatBooked() {
        return seatBooked;
    }

    public void setSeatBooked(String seatBooked) {
        this.seatBooked = seatBooked;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
