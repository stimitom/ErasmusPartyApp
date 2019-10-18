package com.stimitom.erasmuspartyapp;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Venue implements Parcelable{
    private String venueName;
    private String rating;
    private String address;
    private String location;
    private String type;

    private int imageId;
    private int numberOfAttendees;

    private ArrayList<String> openingHours;
    private ArrayList<String> guestList;


    //Default constructor needed for database upload
    public Venue(){}

//    public Venue(String venueName, int imageId, String rating) {
//        this.venueName = venueName;
//        this.imageId = imageId;
//        this.rating = rating;
//        numberOfAttendees = 0;
//        guestList = new ArrayList<String>();
//    }

    public Venue(String venueName, String rating, String address, String location,  ArrayList<String> openingHours, String type) {
        this.venueName = venueName;
        this.rating = rating;
        this.address = address;
        this.location = location;
        this.numberOfAttendees = 0;
        this.openingHours = openingHours;
        this.guestList = new ArrayList<String>();
        this.type = type;

        if (type.equals("BAR"))imageId = R.drawable.ic_local_bar_24dp;
        else if (type.equals("NIGHT_CLUB"))imageId = R.drawable.ic_local_nightclub_24dp;
        else imageId = R.drawable.bk_logo;
    }

    protected Venue(Parcel in) {
        venueName = in.readString();
        rating = in.readString();
        address = in.readString();
        location = in.readString();

        imageId = in.readInt();
        numberOfAttendees = in.readInt();

        type = in.readString();
        openingHours = new ArrayList<String>();
        in.readList(openingHours,null);
        guestList = new ArrayList<String>();
        in.readList(guestList,null);
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(venueName);
        dest.writeString(rating);
        dest.writeString(address);
        dest.writeString(location);
        dest.writeString(type);

        dest.writeInt(imageId);
        dest.writeInt(numberOfAttendees);

        dest.writeStringList(openingHours);
        dest.writeStringList(guestList);
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

    public ArrayList<String> getOpeningHours() {
        return openingHours;
    }

    public String getType() {
        return type;
    }
}
