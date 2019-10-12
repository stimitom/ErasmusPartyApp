package com.stimitom.erasmuspartyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String userid;
    private String username;
    private String nationality;
    private String email;

    private List<String> listnames = new ArrayList<>();
    private long counterpos0;
    private long counterpos1;
    private long counterpos2;
    private Map<String,String> countermapping = new HashMap<>();

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

    public String getUsername() {
        return username;
    }

    public String getNationality() {
        return nationality;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getListnames() {
        return listnames;
    }

    public long getCounterpos0() {
        return counterpos0;
    }

    public long getCounterpos1() {
        return counterpos1;
    }

    public long getCounterpos2() {
        return counterpos2;
    }

    public Map<String, String> getCountermapping() {
        return countermapping;
    }
}
