package com.example.kevin.studyhelper;

import java.util.ArrayList;

/**
 * Created by Kevin on 4/30/2017.
 */

public class User {

    private String username;
    private String password;

    ArrayList<ArrayList<Card>> cardSets;

    public User(){

        cardSets = new ArrayList<ArrayList<Card>>();
    }

    public User(String username, String password){

        this.username = username;
        this.password = password;
        cardSets = new ArrayList<ArrayList<Card>>();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<ArrayList<Card>> getCardSets() {
        return cardSets;
    }

    public void setCardSets(ArrayList<ArrayList<Card>> cardSets) {
        this.cardSets = cardSets;
    }
}
