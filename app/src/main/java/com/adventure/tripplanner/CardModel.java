package com.adventure.tripplanner;

//model class for recycler card which provides trip info.
public class CardModel {
    private String image;
    private String destination;
    private String team;

    public CardModel(String image, String destination, String team) {
        this.image = image;
        this.destination = destination;
        this.team = team;
    }
    public CardModel(){};

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
