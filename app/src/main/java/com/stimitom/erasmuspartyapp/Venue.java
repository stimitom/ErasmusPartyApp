package com.stimitom.erasmuspartyapp;
import java.util.ArrayList;
import java.util.List;

public class Venue {
    private String venueName;
    private String rating;
    private String address;
    private String location;
    private String type;

    private int imageId;
    private int numberOfAttendees;

    private ArrayList<String> openingHours;
    private ArrayList<String> guestList;
    private ArrayList<String> nationalitiesList;


    //Default constructor needed for database upload
    public Venue(){}

    public Venue(String venueName, String rating, String address, String location,  ArrayList<String> openingHours, String type) {
        this.venueName = venueName;
        this.rating = rating;
        this.address = address;
        this.location = location;
        this.numberOfAttendees = 0;
        this.openingHours = openingHours;
        this.guestList = new ArrayList<String>();
        this.nationalitiesList = new ArrayList<String>();
        this.type = type;

            if (type.equals("BAR"))imageId = R.drawable.icons8_beer_glass_48_orange;
            else if (type.equals("NIGHT_CLUB"))imageId = R.drawable.icons8_party_96_blue_orange;
            else imageId = R.drawable.icons8_disco_ball_100;
    }

    public String getVenueName() {
        return venueName;
    }

    public int getImageId() {
        return imageId;
    }

    public String getRating() {
        return rating;
    }

    public int getNumberOfAttendees() { return numberOfAttendees; }

    public List<String> getGuestList() {
        return guestList;
    }

    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getOpeningHours() {
        return openingHours;
    }

    public String getType() {
        return type;
    }

    public List<String> getNationalitiesList() {
        return nationalitiesList;
    }
}
