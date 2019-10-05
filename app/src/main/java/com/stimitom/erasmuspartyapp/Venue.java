package com.stimitom.erasmuspartyapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

//List Item of VenuesListActivity
// implements Parcelable so it can be sent to other activities via an Intent

public class Venue implements Parcelable{
    private String venueName;
    private int imageId;
    private String rating;
    private int numberOfAttendees;
    private List<String> guestList;


    //Default constructor needed for database upload
    public Venue(){}

    public Venue(String venueName, int imageId, String rating) {
        this.venueName = venueName;
        this.imageId = imageId;
        this.rating = rating;
        numberOfAttendees = 0;
        guestList = new ArrayList<String>();
    }

    protected Venue(Parcel in) {
        venueName = in.readString();
        imageId = in.readInt();
        rating = in.readString();
        numberOfAttendees = in.readInt();
        guestList = in.readArrayList(ClassLoader.getSystemClassLoader());
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
        dest.writeInt(imageId);
        dest.writeString(rating);
        dest.writeInt(numberOfAttendees);
        dest.writeList(guestList);
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


}
