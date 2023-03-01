package com.adventure.tripplanner;

//model class for seat booking by traveler.
public class BookingModel {
    private String name;
    private String amount;
    private String seatsBooked;
    private String bookingDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(String seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public BookingModel(String name, String amount, String seatsBooked, String bookingDate) {
        this.name = name;
        this.amount = amount;
        this.seatsBooked = seatsBooked;
        this.bookingDate=bookingDate;
    }
    public BookingModel(){}

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}
