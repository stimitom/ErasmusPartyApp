package com.stimitom.erasmuspartyapp;

import java.util.List;

public class User {
    private String userid;
    private String username;
    private String nationality;
    private String email;
    private List<String> dateList;
    private List<String> venuesattendingTD;
    private int venuecountTD;
    private List<String> venuesattendingTM;
    private int venuecountTM;
    private List<String> venuesattendingTDAT;
    private int venuecountTDAT;


    public User() {
    }

    public User(String userID, String userName, String nationality, String email) {
        this.userid = userID;
        this.username = userName;
        this.nationality = nationality;
        this.email = email;
    }

    public User(String username, String email) {
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

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public List<String> getVenuesattendingTD() {
        return venuesattendingTD;
    }

    public void setVenuesattendingTD(List<String> venuesattendingTD) {
        this.venuesattendingTD = venuesattendingTD;
    }

    public int getVenuecountTD() {
        return venuecountTD;
    }

    public void setVenuecountTD(int venuecountTD) {
        this.venuecountTD = venuecountTD;
    }

    public List<String> getVenuesattendingTM() {
        return venuesattendingTM;
    }

    public void setVenuesattendingTM(List<String> venuesattendingTM) {
        this.venuesattendingTM = venuesattendingTM;
    }

    public int getVenuecountTM() {
        return venuecountTM;
    }

    public void setVenuecountTM(int venuecountTM) {
        this.venuecountTM = venuecountTM;
    }

    public List<String> getVenuesattendingTDAT() {
        return venuesattendingTDAT;
    }

    public void setVenuesattendingTDAT(List<String> venuesattendingTDAT) {
        this.venuesattendingTDAT = venuesattendingTDAT;
    }

    public int getVenuecountTDAT() {
        return venuecountTDAT;
    }

    public void setVenuecountTDAT(int venuecountTDAT) {
        this.venuecountTDAT = venuecountTDAT;
    }
}
