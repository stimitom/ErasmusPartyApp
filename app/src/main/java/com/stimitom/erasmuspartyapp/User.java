package com.stimitom.erasmuspartyapp;

import java.util.List;

public class User {
    private String userid;
    private String username;
    private String nationality;
    private String email;
    private List<String> venuesattending;
    private int venuecount;

    public User(){}

    public User(String userID, String userName, String nationality, String email){
        this.userid = userID;
        this.username = userName;
        this.nationality = nationality;
        this.email = email;
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getVenuesattending() {
        return venuesattending;
    }

    public void setVenuesattending(List<String> venuesattending) {
        this.venuesattending = venuesattending;
    }

    public int getVenuecount() {
        return venuecount;
    }

    public void setVenuecount(int venuecount) {
        this.venuecount = venuecount;
    }
}
